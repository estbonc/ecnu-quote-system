package com.juran.quote.service;

import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.enums.MainMaterialEnum;
import com.juran.quote.bean.enums.MaterialTypeEnum;
import com.juran.quote.bean.enums.QuoteErrorEnum;
import com.juran.quote.bean.enums.QuoteHouseEnum;
import com.juran.quote.bean.enums.QuoteRoomEnum;
import com.juran.quote.bean.enums.SpaceMappingEnum;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.QuoteRoomConstructPo;
import com.juran.quote.bean.po.QuoteRoomMapPo;
import com.juran.quote.bean.po.QuoteTypePo;
import com.juran.quote.bean.quote.AdditionalDto;
import com.juran.quote.bean.quote.BomDto;
import com.juran.quote.bean.quote.BomItemDto;
import com.juran.quote.bean.quote.BomRoomDto;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/28 10:28
 * @description
 */
public class BaseService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PropertiesUtils propertiesUtils;

    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    /**
     * 排除不需要解析的bomList中指定type
     */
    protected List<String> excludeBomTypes = Arrays.asList(MainMaterialEnum.D3_QIANG_QI.getCode(), MainMaterialEnum.D3_SHI_GAO_XIAN.getCode());

    /**
     * 定义需要解析bomList中软装的type
     */
    protected List<String> productBomType = Arrays.asList(MaterialTypeEnum.FURNITURE.getType(), MaterialTypeEnum.ELECTRONICS.getType(), MaterialTypeEnum.OTHER.getType());

    /**
     * 获取房型对应的空间列表
     *
     * @param houseType 房型标识
     * @return
     */
    public QuoteHouseEnum getQuoteRooms(String houseType) throws QuoteException {
        try {
            return Enum.valueOf(QuoteHouseEnum.class, houseType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new QuoteException(QuoteErrorEnum.QUOTE_HOUSE_ROOM_MAP_ERROR);
        }
    }

    /**
     * 获取3d设计和空间枚举定义的映射
     *
     * @param qrList 空间映射
     * @return 3d设计空间编号#空间枚举
     */
    public Map<String, QuoteRoomEnum> getDesignQuoteRoomMap(List<QuoteRoomMapPo> qrList) {
        if (CollectionUtils.isEmpty(qrList)) {
            return Collections.EMPTY_MAP;
        }
        Map<String, QuoteRoomEnum> roomMap = new HashMap<>(qrList.size());
        for (QuoteRoomMapPo qr : qrList) {
            roomMap.put(qr.getDesignRoomId(), Enum.valueOf(QuoteRoomEnum.class, qr.getRoomId()));
        }
        return roomMap;
    }


    /**
     * 过滤不参与计算的bomList清单项
     *
     * @param bomList 过滤后
     */
    public List<BomItemDto> filterBomList(List<BomItemDto> bomList) {
        return bomList.stream().filter(bi -> !StringUtils.isEmpty(bi.getType())
                && !excludeBomTypes.contains(bi.getType()) && !StringUtils.isEmpty(bi.getRoomID())).collect(Collectors.toList());
    }


    /**
     * 去重同一空间，重复的sku商品，数量合并
     *
     * @param bomList 3d-原始bom清单
     */
    public void trimBomList(List<BomItemDto> bomList) {
        // 按空间去重，数量合并，过滤roomID空/Name的数据，直接丢弃
        Map<String, List<BomItemDto>> roomBomMap = bomList.stream().filter(bi -> !StringUtils.isEmpty(bi.getRoomID()) && !StringUtils.isEmpty(bi.getName()))
                .collect(Collectors.groupingBy(BomItemDto::getRoomID));
        // 清空,去重,合并
        bomList.clear();
        // 按空间去重
        roomBomMap.forEach((String roomId, List<BomItemDto> roomBomList) -> {
            // sku空,直接添加
            List<BomItemDto> emptySkuAndBrandBomList = roomBomList.stream().filter(bi -> StringUtils.isEmpty(bi.getSku()) && StringUtils.isEmpty((bi.getBrand())))
                    .collect(Collectors.toList());

            List<BomItemDto> emptySkuBomList = roomBomList.stream().filter(bi -> StringUtils.isEmpty(bi.getSku()) && !StringUtils.isEmpty((bi.getBrand())))
                    .collect(Collectors.toList());
            // sku无值，按照名称和品牌去重
            List<BomItemDto> emptySkuFinallyBomList = new ArrayList<>();
            //step1 按照品牌groupBy
            Map<String, List<BomItemDto>> repeatedBrandBomMap = emptySkuBomList.stream()
                    .filter(bi -> StringUtils.isEmpty(bi.getSku())).collect(Collectors.groupingBy(BomItemDto::getBrand));
            repeatedBrandBomMap.forEach((brand, tmpBomList) -> {
                //step2 按照名称groupBy
                Map<String, List<BomItemDto>> repeatedNameBomMap = tmpBomList.stream()
                        .filter(bi -> StringUtils.isEmpty(bi.getSku())).collect(Collectors.groupingBy(BomItemDto::getName));
                repeatedBomList(repeatedNameBomMap, emptySkuFinallyBomList);
            });
            bomList.addAll(emptySkuFinallyBomList);
            List<BomItemDto> emptySkuAndBrandFinallyBomList = new ArrayList<>();
            Map<String, List<BomItemDto>> emptySkuAndBrandFinallyBomMap = emptySkuAndBrandBomList.stream()
                    .filter(bi -> StringUtils.isEmpty(bi.getSku())).collect(Collectors.groupingBy(BomItemDto::getName));
            repeatedBomList(emptySkuAndBrandFinallyBomMap, emptySkuAndBrandFinallyBomList);
            bomList.addAll(emptySkuAndBrandFinallyBomList);

            // sku有值,合并去重
            Map<String, List<BomItemDto>> repeatedSkuBomMap = roomBomList.stream()
                    .filter(bi -> !StringUtils.isEmpty(bi.getSku())).collect(Collectors.groupingBy(BomItemDto::getSku));
            repeatedBomList(repeatedSkuBomMap, bomList);
        });
    }


    /**
     * 从3Dbom里根据SKU和空间信息筛选BoomItemDto
     *
     * @param sku
     * @param roomId
     * @return
     */
    public BomItemDto getBomItemFrom3DBom(String sku, String roomId, List<BomItemDto> realBiList, List<QuoteRoomMapPo> roomMap) {
        if (StringUtils.isEmpty(sku)) {
            return null;
        }
        List<String> designRoomId = new ArrayList<>();
        for (QuoteRoomMapPo quoteRoomMapPo : roomMap) {
            if (roomId.equals(quoteRoomMapPo.getRoomId())) {
                designRoomId.add(quoteRoomMapPo.getDesignRoomId());
            }
        }
        List<BomItemDto> collect = realBiList.stream().filter(bomItemDto ->
                sku.equals(bomItemDto.getSku()) && designRoomId.contains(bomItemDto.getRoomID())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)) {
            return collect.get(0);
        }
        return null;
    }


    /**
     * 从3Dbom里根据SKU和空间信息筛选BoomItemDto
     *
     * @param roomId
     * @param realBiList
     * @param roomMap
     * @return
     */
    public List<BomItemDto> getBomItemFrom3DBom(String roomId, List<BomItemDto> realBiList, List<QuoteRoomMapPo> roomMapping) {
        List<String> designRoomId = roomMapping.stream()
                .filter( room -> roomId.equals(room.getRoomId()))
                .map(QuoteRoomMapPo::getDesignRoomId)
                .collect(Collectors.toList());
        List<BomItemDto> collect = realBiList.stream().filter(bomItemDto ->
                designRoomId.contains(bomItemDto.getRoomID())).collect(Collectors.toList());
        return collect;
    }

    /**
     * 从3Dbom里根据SKU和空间信息筛选BoomItemDto(筛选软装信息且sku不为空)
     *
     * @param collect
     * @return
     */
    public List<BomItemDto> getProductBomItemFrom3DBom(List<BomItemDto> collect) {
        List<String> category = MaterialTypeEnum.getCategory(productBomType);
        List<BomItemDto> list = collect.stream().filter(bomItemDto -> category.contains(bomItemDto.getType())).collect(Collectors.toList());
        return list;
    }


    /**
     * 初始化3D-bom清单中的房型空间匹配，只匹配3d携带的空间数据
     *
     * @param bom 3d-bom原始数据
     * @return
     */
    public List<QuoteRoomMapPo> initSpaceMapping(BomDto bom) {
        List<QuoteRoomMapPo> qrList = Collections.EMPTY_LIST;
        if (!CollectionUtils.isEmpty(bom.getRoomList())) {
            qrList = new ArrayList<>(bom.getRoomList().size());
            for (BomRoomDto room : bom.getRoomList()) {
                qrList.add(autoSpaceMapping(room));
            }
        }
        return qrList;
    }

    /**
     * 自动空间匹配映射
     *
     * @param room 3d-room信息
     * @return
     */
    private QuoteRoomMapPo autoSpaceMapping(BomRoomDto room) {
        QuoteRoomMapPo qr = new QuoteRoomMapPo();

        SpaceMappingEnum mp = SpaceMappingEnum.UNKNOW;
        if (!StringUtils.isEmpty(room.getRoomType())) {
            try {
                mp = Enum.valueOf(SpaceMappingEnum.class, room.getRoomType().toUpperCase());
            } catch (IllegalArgumentException e) {
                mp = SpaceMappingEnum.UNKNOW;
            }
        }

        qr.setDesignRoomId(String.valueOf(room.getRoomID()));
        qr.setDesignRoomName(mp.getDesignName());
        try {
            qr.setRoomSize(new BigDecimal(room.getArea()));
        } catch (Exception e) {
            qr.setRoomSize(new BigDecimal(0d));
        }
        if (!mp.getQuoteSpace().getId().equals(QuoteRoomEnum.OTHERS.getId())) {
            qr.setRoomId(mp.getQuoteSpace().getId());
            qr.setRoomName(mp.getQuoteSpace().getName());
        }

        return qr;
    }

    private void repeatedBomList(Map<String, List<BomItemDto>> repeatedBomMap, List<BomItemDto> bomList) {
        repeatedBomMap.forEach((sku, tmpBomList) -> {
            BomItemDto bi = tmpBomList.get(0);
            // 数量合并
            BigDecimal quantity = BigDecimal.ZERO;
            // 金额合并
            BigDecimal price = BigDecimal.ZERO;
            // 砖类主材片数合并
            BigDecimal tileNumber = BigDecimal.ZERO;
            // 砖类主材铺贴面积合并
            BigDecimal tileArea = BigDecimal.ZERO;

            for (BomItemDto tmp : tmpBomList) {
                if (tmp.getQuantity() != null) {
                    quantity = quantity.add(tmp.getQuantity());
                }
                if (!StringUtils.isEmpty(tmp.getPrice())) {
                    price = price.add(new BigDecimal(tmp.getPrice()));
                }
                if (tmp.getAddtional() != null) {
                    if (tmp.getAddtional().getTileNumber() != null) {
                        tileNumber = tileNumber.add(tmp.getAddtional().getTileNumber());
                    }
                    if (tmp.getAddtional().getArea() != null) {
                        tileArea = tileArea.add(tmp.getAddtional().getArea());
                    }
                }
            }
            bi.setQuantity(quantity);
            bi.setPrice(price.toString());
            AdditionalDto biAdd = bi.getAddtional();
            if (biAdd != null) {
                biAdd.setTileNumber(tileNumber);
                biAdd.setArea(tileArea);
            }

            bomList.add(bi);
        });
    }

    /**
     * 处理施工项去重根据cid，limit取最大值，useQuantity相加，然后判断出超量的施工项
     * 超量的处理
     *
     * @param constructionItems
     */
    public List<QuoteRoomConstructPo> repeatedQuoteRoomConstruct(List<QuoteRoomConstructPo> constructionItems) {
        List<QuoteRoomConstructPo> result = new ArrayList<>(constructionItems);
        Collections.copy(constructionItems, result);
        //施工项去重，根据cid，limit相加，useQuantity相加，然后判断出超量的施工项
        if (!CollectionUtils.isEmpty(result)) {
            Map<Long, List<QuoteRoomConstructPo>> constructionMap = result.stream().collect(Collectors.groupingBy(QuoteRoomConstructPo::getCid));
            result.clear();
            constructionMap.forEach((constructId, construcList) -> {
                QuoteRoomConstructPo first = construcList.get(0);
                BigDecimal limit = first.getLimit();
                BigDecimal useQuantity = BigDecimal.valueOf(construcList.stream().mapToDouble(con -> con.getUsed().doubleValue()).sum());
                first.setLimit(limit);
                first.setUsed(useQuantity);
                result.add(first);
            });
        }
        return result;
    }

    protected <T> T findPoByUniqueKey(Properties properties, Class<T> poClass) throws QuoteException {
        try {
            Criteria criteria = new Criteria();
            properties.keySet().stream().forEach( key -> {
                criteria.and((String) key).is(properties.get(key));
            });
            Query query = new Query(criteria);
            return mongoTemplate.findOne(query, poClass);
        } catch (Exception e) {
            logger.error("查询异常{}", e.getMessage());
            throw new QuoteException("查询异常");
        }
    }

    protected <T> List<T> findPoList(Properties properties, Class<T> poClass) throws QuoteException {
        try {
            Criteria criteria = new Criteria();
            properties.keySet().stream().forEach( key -> {
                if(properties.get(key) instanceof Collection) {
                    criteria.and((String) key).in(properties.get(key));
                } else {
                    criteria.and((String) key).is(properties.get(key));
                }
            });
            Query query = new Query(criteria);
            return mongoTemplate.find(query, poClass);
        } catch (Exception e) {
            logger.error("查询异常{}", e.getMessage());
            throw new QuoteException("查询异常");
        }
    }
}
