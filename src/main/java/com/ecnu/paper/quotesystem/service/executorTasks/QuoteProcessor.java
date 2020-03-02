package com.ecnu.paper.quotesystem.service.executorTasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.mdm.client.bean.response.AttrInfoGetResponseBean;
import com.juran.mdm.client.bean.response.AttrValGetRespBean;
import com.juran.mdm.client.bean.response.CatentryGetSPURspBean;
import com.juran.mdm.client.feign.CatentryFeignClient;
import com.juran.quote.bean.dto.DecorationMaterial;
import com.juran.quote.bean.dto.QuoteRoomDto;
import com.juran.quote.bean.dto.TaoBaoResponseData;
import com.juran.quote.bean.dto.TaoBaoResponseItemData;
import com.juran.quote.bean.enums.MaterialTypeEnum;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.QuoteDocumentPo;
import com.juran.quote.bean.quote.BomItemDto;
import com.juran.quote.bean.quote.BomRoomDto;
import com.juran.quote.bean.request.CreateQuoteRequestBean;
import com.juran.quote.service.BaseService;
import com.juran.quote.service.MaterialService;
import com.juran.quote.service.TaoBaoService;
import com.juran.quote.utils.ClassUtil;
import com.juran.quote.utils.MDQueryUtil;
import com.juran.quote.utils.RedisLock;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuoteProcessor extends BaseService {
    @Autowired
    TaoBaoService taoBaoService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MaterialService materialService;
    @Autowired
    CatentryFeignClient catentryFeignClient;

    @Autowired
    private RedisLock redisLock;


    @Async("taskExecutor")
    public void buildQuoteRooms(CreateQuoteRequestBean bom, QuoteDocumentPo quote) throws QuoteException {
        try {
            if (redisLock.acquire(quote.getQuoteId().toString())) {
                //如果报价不包含房间说明该案例是第一次做报价，创建空的空间列表
                logger.info("开始异步解析bom，报价id{}", quote.getQuoteId());
                if (CollectionUtils.isEmpty(quote.getRooms())) {
                    quote.setRooms(new ArrayList<>());
                }
                //房屋面积全部置零，以便后面房间合并时面积累加
                quote.getRooms().forEach(room -> room.setArea(BigDecimal.ZERO));

                for (BomRoomDto bomRoom : bom.getRoomList()) {
                    Optional<QuoteRoomDto> room = quote.getRooms()
                            .stream()
                            .filter(rm -> bomRoom.getRoomType().equals(rm.getRoomType()) &&
                                    bomRoom.getRoomName().equals(rm.getRoomName()))
                            .findFirst();
                    //如果报价已经包含该房间，更新该房间的报价，否则新创建房间
                    if (room.isPresent()) {
                        //房间面积累加
                        BigDecimal roomArea = room.get().getArea().add(BigDecimal.valueOf(Double.valueOf(bomRoom.getArea())));
                        room.get().setArea(roomArea.setScale(2, BigDecimal.ROUND_HALF_UP));
                        //更新商品信息
                        updateMaterialInQuoteRoom(bom.getBomList(), room.get());
                    } else {
                        //创建新房间
                        QuoteRoomDto quoteRoom = new QuoteRoomDto();
                        quoteRoom.setRoomType(bomRoom.getRoomType());
                        quoteRoom.setRoomID(bomRoom.getRoomID());
                        quoteRoom.setRoomName(bomRoom.getRoomName());
                        quoteRoom.setArea(BigDecimal.valueOf(Double.valueOf(bomRoom.getArea())).setScale(2, BigDecimal.ROUND_HALF_UP));
                        quoteRoom.setHeight(BigDecimal.valueOf(Double.valueOf(bomRoom.getHeight())).setScale(2, BigDecimal.ROUND_HALF_UP));
                        quoteRoom.setPerimeter(BigDecimal.valueOf(Double.valueOf(bomRoom.getPerimeter())).setScale(2, BigDecimal.ROUND_HALF_UP));

                        initMaterialInQuoteRoom(bom.getBomList(), bomRoom, quoteRoom);
                        quote.getRooms().add(quoteRoom);
                    }
                }
                WriteResult writeResult = updateQuote(quote, Optional.of("designId"), Optional.of(bom.getDesignID()));
                if (writeResult.getN() == 0) {
                    logger.info("开始报价-解析bom失败,designId:{}.", bom.getDesignID());
                } else {
                    logger.info("开始报价-解析bom完成,designId:{}.", bom.getDesignID());
                }
            }
        } catch (Exception e) {
            logger.error("[QuoteProcessor:buildQuoteRooms]开始报价-解析bom异常", e.getMessage());
            throw new QuoteException("开始报价-解析bom异常");
        } finally {
            redisLock.release(quote.getQuoteId().toString());
        }
    }

    private void updateMaterialInQuoteRoom(List<BomItemDto> bomList, QuoteRoomDto quoteRoomPo) {
        //取出房间内主材
        List<BomItemDto> bomItemsInThisRoom = bomList.stream()
                .filter(bom -> bom.getRoomID().equalsIgnoreCase(quoteRoomPo.getRoomID()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(bomItemsInThisRoom)) {
            quoteRoomPo.setHardDecorationMaterials(Lists.newArrayList());
            quoteRoomPo.setSoftDecorationMaterials(Lists.newArrayList());
            quoteRoomPo.setInvalidHardDecorationMaterials(Lists.newArrayList());
            quoteRoomPo.setInvalidSoftDecorationMaterials(Lists.newArrayList());
            return;
        }

        // 取出历史商品数据
        List<DecorationMaterial> historyHDMaterials = quoteRoomPo.getHardDecorationMaterials();
        List<DecorationMaterial> historySDMaterials = quoteRoomPo.getSoftDecorationMaterials();
        List<DecorationMaterial> historyInvalidHDMaterial = quoteRoomPo.getInvalidHardDecorationMaterials();
        List<DecorationMaterial> historyInvalidSDMaterial = quoteRoomPo.getInvalidSoftDecorationMaterials();

        //所有软装type
        List<String> softMaterialCategories = MaterialTypeEnum.getCategory(productBomType);

        //商品数量清零以方便后面对重复商品数量累加
        quoteRoomPo.getSoftDecorationMaterials().stream().forEach(m -> m.setUsedQuantity(BigDecimal.ZERO));
        quoteRoomPo.getHardDecorationMaterials().stream().forEach(m -> m.setUsedQuantity(BigDecimal.ZERO));
        quoteRoomPo.getInvalidSoftDecorationMaterials().stream().forEach(m -> m.setUsedQuantity(BigDecimal.ZERO));
        quoteRoomPo.getInvalidHardDecorationMaterials().stream().forEach(m -> m.setUsedQuantity(BigDecimal.ZERO));
        //根据bom更新房间内的商品
        bomItemsInThisRoom.forEach(material -> {
            if (softMaterialCategories.contains(material.getType())) {
                mapRoomMaterial(material, quoteRoomPo.getSoftDecorationMaterials(), quoteRoomPo.getInvalidSoftDecorationMaterials());
            } else {
                mapRoomMaterial(material, quoteRoomPo.getHardDecorationMaterials(), quoteRoomPo.getInvalidHardDecorationMaterials());
            }
        });

        //过滤掉上次报价存在但是这次从3D设计删除的商品
        BomItemDto comparedBomItem = new BomItemDto();
        List<DecorationMaterial> newSoftDecorationMaterials = quoteRoomPo.getSoftDecorationMaterials()
                .stream()
                .filter(sm -> {
                    comparedBomItem.setName(sm.getMaterialName());
                    comparedBomItem.setSku(sm.getJuranSku());
                    return bomItemsInThisRoom.contains(comparedBomItem);
                })
                .collect(Collectors.toList());
        quoteRoomPo.setSoftDecorationMaterials(newSoftDecorationMaterials);

        List<DecorationMaterial> newInvalidSoftDecorationMaterials = quoteRoomPo.getInvalidSoftDecorationMaterials()
                .stream()
                .filter(sm -> {
                    comparedBomItem.setName(sm.getMaterialName());
                    comparedBomItem.setSku(sm.getJuranSku());
                    return bomItemsInThisRoom.contains(comparedBomItem);
                })
                .collect(Collectors.toList());
        quoteRoomPo.setInvalidSoftDecorationMaterials(newInvalidSoftDecorationMaterials);

        List<DecorationMaterial> newHardDecorationMaterials = quoteRoomPo.getHardDecorationMaterials()
                .stream()
                .filter(hm -> {
                    comparedBomItem.setName(hm.getMaterialName());
                    comparedBomItem.setSku(hm.getJuranSku());
                    return bomItemsInThisRoom.contains(comparedBomItem);
                })
                .collect(Collectors.toList());
        quoteRoomPo.setHardDecorationMaterials(newHardDecorationMaterials);

        List<DecorationMaterial> newInvalidHardDecorationMaterials = quoteRoomPo.getInvalidHardDecorationMaterials()
                .stream()
                .filter(hm -> {
                    comparedBomItem.setName(hm.getMaterialName());
                    comparedBomItem.setSku(hm.getJuranSku());
                    return bomItemsInThisRoom.contains(comparedBomItem);
                })
                .collect(Collectors.toList());
        quoteRoomPo.setInvalidHardDecorationMaterials(newInvalidHardDecorationMaterials);
    }

    private void initMaterialInQuoteRoom(List<BomItemDto> bomList, BomRoomDto bomRoom, QuoteRoomDto quoteRoom) {
        //获取所有软装type
        List<String> softMaterialCategories = MaterialTypeEnum.getCategory(productBomType);
        //初始化软硬装商品列表
        List<DecorationMaterial> softDecorationMaterials = new ArrayList<>();
        List<DecorationMaterial> hardDecorationMaterials = new ArrayList<>();
        List<DecorationMaterial> invalidSoftDecorationMaterials = new ArrayList<>();
        List<DecorationMaterial> invalidHardDecorationMaterials = new ArrayList<>();
        //根据bom把bomList匹配到房间内商品列表
        bomList.stream()
                .filter(bom -> bom.getRoomID().equalsIgnoreCase(bomRoom.getRoomID()))
                .forEach(material -> {
                    if (softMaterialCategories.contains(material.getType())) {
                        mapRoomMaterial(material, softDecorationMaterials, invalidSoftDecorationMaterials);
                    } else {
                        mapRoomMaterial(material, hardDecorationMaterials, invalidHardDecorationMaterials);
                    }
                });
        quoteRoom.setSoftDecorationMaterials(softDecorationMaterials);
        quoteRoom.setHardDecorationMaterials(hardDecorationMaterials);
        quoteRoom.setInvalidSoftDecorationMaterials(invalidSoftDecorationMaterials);
        quoteRoom.setInvalidHardDecorationMaterials(invalidHardDecorationMaterials);
    }

    private void mapRoomMaterial(BomItemDto bomMaterial, List<DecorationMaterial> quoteMaterials, List<DecorationMaterial> invalidMaterials) {
        DecorationMaterial decorationMaterial = new DecorationMaterial();
        decorationMaterial.setMaterialName(bomMaterial.getName());
        decorationMaterial.setJuranSku(bomMaterial.getSku());

        //判断房间内是否存在该商品，如果存在就对数量进行累加，否则新初始化商品信息
        if (quoteMaterials.contains(decorationMaterial)) {
            DecorationMaterial historyMaterial =
                    quoteMaterials.get(quoteMaterials.indexOf(decorationMaterial));
            historyMaterial.setUsedQuantity(historyMaterial
                    .getUsedQuantity()
                    .add(bomMaterial.getQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        } else if (invalidMaterials.contains(decorationMaterial)) {
            DecorationMaterial historyMaterial =
                    invalidMaterials.get(invalidMaterials.indexOf(decorationMaterial));
            historyMaterial.setUsedQuantity(historyMaterial
                    .getUsedQuantity()
                    .add(bomMaterial.getQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            decorationMaterial.setCategoryId(bomMaterial.getCategoryId());
            decorationMaterial.setImageUrl(bomMaterial.getImageUrl());
            decorationMaterial.setBrand(bomMaterial.getBrand());
            decorationMaterial.setAliModelId(bomMaterial.getModelId());
            decorationMaterial.setUsedQuantity(bomMaterial.getQuantity()
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
            decorationMaterial.setUnit(bomMaterial.getUnit());
            // toDo: 通过modelId获取商品信息
            TaoBaoResponseData taoBaoResponse = taoBaoService.getTBDataByModelId(bomMaterial.getModelId());
            if (taoBaoResponse != null && !CollectionUtils.isEmpty(taoBaoResponse.getItems())) {
                long price = 0l;
                for (TaoBaoResponseItemData itemData : taoBaoResponse.getItems()) {
                    if (itemData.getOriginPrice() != null && itemData.getOriginPrice() != 0l) {
                        price = itemData.getOriginPrice() / 100;
                        break;
                    }
                }
                if (price == 0l) {
                    decorationMaterial.setUnitPrice((!StringUtils.isEmpty(bomMaterial.getSku()) ? materialService.getPriceByMdmSku(bomMaterial.getSku()) : BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    decorationMaterial.setUnitPrice(new BigDecimal(price)
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            } else {
                decorationMaterial.setUnitPrice((!StringUtils.isEmpty(bomMaterial.getSku()) ? materialService.getPriceByMdmSku(bomMaterial.getSku()) : BigDecimal.ZERO)
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            // sku不为空
            // 获取商品的型号和规格信息
            if (!StringUtils.isEmpty(decorationMaterial.getJuranSku())) {
                try {
                    CatentryGetSPURspBean catentryGetSPURspBean = catentryFeignClient.retrieveCatentry(decorationMaterial.getJuranSku(), Boolean.FALSE);
                    //1.获取型号
                    if (catentryGetSPURspBean != null && catentryGetSPURspBean.getIteminfo() != null) {
                        decorationMaterial.setModel(catentryGetSPURspBean.getIteminfo().getModel());
                    }
                    //2.获取规格
                    List<AttrInfoGetResponseBean> attrs = catentryGetSPURspBean != null ? catentryGetSPURspBean.getAttr() : Lists.newArrayList();
                    Optional<AttrInfoGetResponseBean> attr = Optional.empty();
                    if (!CollectionUtils.isEmpty(attrs)) {
                        attr = attrs.stream().filter(p -> "规格".equals(p.getAttrName())).findFirst();
                    }
                    if (attr.isPresent()) {
                        List<AttrValGetRespBean> listAttrVals = catentryGetSPURspBean.getListAttrVal();
                        if (!CollectionUtils.isEmpty(listAttrVals)) {
                            for (AttrValGetRespBean bean : listAttrVals) {
                                if (!StringUtils.isEmpty(attr.get().getAttrKey()) && attr.get().getAttrKey().equals(bean.getAttrKey())) {
                                    decorationMaterial.setSpec(bean.getDescription());
                                }
                            }

                        }
                    }
                } catch (RuntimeException e) {
                    logger.info("sku:{}查询商品规格信息异常{}", decorationMaterial.getJuranSku(), e.getMessage());
                    decorationMaterial.setSpec("--");
                    decorationMaterial.setModel("--");
                }
            } else {
                decorationMaterial.setSpec("--");
                decorationMaterial.setModel("--");
            }
            if (decorationMaterial.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {
                invalidMaterials.add(decorationMaterial);
            } else {
                quoteMaterials.add(decorationMaterial);
            }
        }
    }

    private WriteResult updateQuote(QuoteDocumentPo quotePo, Optional<String> paramKey, Optional<Object> paramValue) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(paramKey.get(), paramValue.get());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(quotePo, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, QuoteDocumentPo.class);
        return writeResult;
    }
}
