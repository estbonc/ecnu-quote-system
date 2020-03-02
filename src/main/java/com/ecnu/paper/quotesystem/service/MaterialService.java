package com.ecnu.paper.quotesystem.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.core.exception.ParentException;
import com.juran.mdm.price.client.bean.response.PriceViewResponseBean;
import com.juran.mdm.price.client.feign.IPriceFeignClient;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.enums.QuoteUnitEnum;
import com.juran.quote.bean.enums.RedisPreEnum;
import com.juran.quote.bean.po.Brand;
import com.juran.quote.bean.po.PricePo;
import com.juran.quote.bean.po.Product;
import com.juran.quote.bean.po.SelectedMaterial;
import com.juran.quote.bean.request.MaterialRequestBean;
import com.juran.quote.config.Constants;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaterialService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    IPriceFeignClient priceFeignClient;
    @Autowired
    RedisService redisService;

    @Autowired
    private KeyManagerUtil keyManagerUtil;

    public void insert(SelectedMaterial selectedMaterial) throws ParentException {
        mongoTemplate.insert(selectedMaterial);
    }

    public int delete(String id) {
        return 0;
    }

    public int update(SelectedMaterial selectedMaterial) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("materialId", selectedMaterial.getMaterialId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        //构建更新
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(selectedMaterial, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, SelectedMaterial.class);
        return writeResult.getN();
    }

    public SelectedMaterial getById(String id) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("materialId", Long.valueOf(id));
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, SelectedMaterial.class);
    }

    public List<SelectedMaterial> queryMaterialList(List<Long> idList) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("materialId", new BasicDBObject("$in", idList));
        return mongoTemplate.find(new BasicQuery(basicDBObject), SelectedMaterial.class);
    }

    public List<SelectedMaterial> queryDistinctMaterial() {
        List<String> materialList = mongoTemplate.getCollection("material").distinct("materialCode");
        logger.info("数据库主材去重个数:{}", materialList.size());
        List<SelectedMaterial> resList = Lists.newArrayList();
        for (String code : materialList) {
            SelectedMaterial material = this.queryOneMaterialByCode(code);
            resList.add(material);
        }
        return resList;
    }

    public SelectedMaterial queryOneMaterialByCode(String code) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("materialCode", code);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, SelectedMaterial.class);
    }

    public List<SelectedMaterial> queryMaterialByIdsAndConstructId(List<Long> materialIdList, Long constructId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("selectedConstruct.cid", constructId);
        Query basicQuery = MDQueryUtil.batchGetDBObjectByListAndMap("materialId", materialIdList, valuesMap);
        return mongoTemplate.find(basicQuery, SelectedMaterial.class);
    }

    @Deprecated
    public BigDecimal getPriceBySku(String sku) {
        BigDecimal price = BigDecimal.ZERO;
        try {
            String stringPrice = redisService.getString(RedisPreEnum.MATERIAL_PRICE.getPre() + sku);
            if (StringUtils.isNotBlank(stringPrice)) {
                price = new BigDecimal(stringPrice);
            } else {
                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.put("productId", sku);
                PricePo queryPrice = mongoTemplate.findOne(new BasicQuery(basicDBObject), PricePo.class);
                if (queryPrice != null) {
                    pushRedis(queryPrice);
                    price = queryPrice.getPrices().get(0).getPrice();
                } else {
                    logger.info("本地没有{}的价格信息，去主数据取===》", sku);
                    PriceViewResponseBean priceViewResponseBean = priceFeignClient.getPriceByProductId(sku, Constants.REGION_CODE);
                    PricePo pricePo = insertOrUpdateMaterialPrice(priceViewResponseBean);
                    pushRedis(pricePo);
                    if (pricePo != null) {
                        price = pricePo.getPrices().get(0).getPrice();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("通过sku:{}获取主材出现异常:{}", sku, e);
        }
        return price;
    }

    /**
     * 通过住数据sku获取价格
     *
     * @param sku
     * @return
     */
    public BigDecimal getPriceByMdmSku(String sku) {
        BigDecimal price = BigDecimal.ZERO;
        try {
            PriceViewResponseBean priceViewResponseBean = priceFeignClient.getPriceByProductId(sku, Constants.REGION_CODE);
            if(priceViewResponseBean == null || CollectionUtils.isEmpty(priceViewResponseBean.getPrices())){
                logger.info("主材sku：{},价格不存在",sku);
                return price;
            }
            price = priceViewResponseBean.getPrices().get(0).getPrice();
        } catch (Exception e) {
            logger.error("通过sku:{}获取主材出现异常:{}", sku, e);
        }
        return price;
    }


    @Deprecated
    private void pushRedis(PricePo price) {
        if (price.getPrices() != null && !price.getPrices().isEmpty()) {
            redisService.setString(RedisPreEnum.MATERIAL_PRICE.getPre() + price.getProductId(), price.getPrices().get(0).getPrice().toString());
        }
    }


    /**
     * 主材价格入库
     *
     * @param priceViewResponseBean
     * @return
     */
    @Deprecated
    private PricePo insertOrUpdateMaterialPrice(PriceViewResponseBean priceViewResponseBean) {
        PricePo pricePo = new PricePo();
        BeanUtils.copyProperties(priceViewResponseBean, pricePo);
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put("productId", pricePo.getProductId());
            PricePo queryPo = mongoTemplate.findOne(new BasicQuery(basicDBObject), PricePo.class);
            if (queryPo != null) {
                Update update = Update.update("prices", pricePo.getPrices());
                WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, PricePo.class);
            } else {
                mongoTemplate.insert(pricePo);
            }
        } catch (Exception e) {
            logger.error("主材价格{}入库发生异常:{}", pricePo.getProductId(), e);
            return null;
        }
        return pricePo;
    }


    /**
     * 录入主材
     *
     * @param materialRequestBean
     * @return
     * @throws ParentException
     */
    @LogAnnotation
    public ServiceResponse createMaterial(MaterialRequestBean materialRequestBean) throws PackageException {
        //1.创建品牌
        String message;
        try {
            Criteria criteria = Criteria.where(CommonStaticConst.Package.PACKAGE_ROOM_ID).is(materialRequestBean.getPackageRoomId())
                    .and(CommonStaticConst.Package.MATERIAL_CODE).is(materialRequestBean.getMaterialCode());
            Query query = new Query(criteria);
            List<SelectedMaterial> materials = mongoTemplate.find(query, SelectedMaterial.class);
            SelectedMaterial insertMaterial;
            if (CollectionUtils.isEmpty(materials)) {
                //没有主材就需要一下吧主材品牌商品一起录进去
                insertMaterial = buildMaterial(materialRequestBean);
                mongoTemplate.insert(insertMaterial);
            } else {
                SelectedMaterial selectedMaterial = materials.get(0);
                List<Brand> brands = selectedMaterial.getBrands();
                List<Brand> selectBrands = brands.parallelStream().filter(brand -> brand.getBrandCode().equals(materialRequestBean.getBrandCode())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(selectBrands)) {
                    Brand brand = buildBrand(materialRequestBean);
                    selectedMaterial.getBrands().add(brand);
                } else {
                    Brand selectBrand = selectBrands.get(0);
                    List<Product> products = selectBrand.getProduct();
                    List<Product> selectProduct = products.parallelStream().filter(product -> product.getPCode().equals(materialRequestBean.getProductCode())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(selectProduct)) {
                        Product product = buildProduct(materialRequestBean);
                        selectBrand.getProduct().add(product);
                    }
                }
                Update update = new Update();
                update.set(CommonStaticConst.Package.BRANDS, selectedMaterial.getBrands());
                WriteResult writeResult = mongoTemplate.updateMulti(query, update, SelectedMaterial.class);
                if (writeResult.getN() == 0) {
                    return new ServiceResponse(false, "主材创建失败");
                }
            }
        } catch (DataAccessException e) {
            message = String.format("创建主材发生异常{}", e);
            logger.error("房型{}创建主材{}出现异常：{}", materialRequestBean.getPackageRoomId(), materialRequestBean.getMaterialId(), e);
            throw new PackageException(message);
        }
        return new ServiceResponse(true, "主材创建成功");
    }

    /**
     * 组装产品数据
     *
     * @param materialRequestBean
     * @return
     */
    private Product buildProduct(MaterialRequestBean materialRequestBean) {
        Product product = new Product();
        product.setPCode(materialRequestBean.getProductCode());
        product.setPName(materialRequestBean.getProductName());
        product.setModel(materialRequestBean.getProductModel());
        product.setPrice(materialRequestBean.getProductPrice());
        product.setSpec(materialRequestBean.getProductSpec());
        product.setCoho(materialRequestBean.getCoho());
        product.setMdmSku(materialRequestBean.getMdmSku());
        return product;
    }

    /**
     * 组装品牌数据
     *
     * @param materialRequestBean
     * @return
     */
    private Brand buildBrand(MaterialRequestBean materialRequestBean) {
        Brand brand = new Brand();
        brand.setBrandCode(materialRequestBean.getBrandCode());
        brand.setBrandName(materialRequestBean.getBrandName());
        Product product = buildProduct(materialRequestBean);
        brand.getProduct().add(product);
        return brand;
    }


    /**
     * 组装主材数据
     *
     * @param materialRequestBean
     * @return
     */
    private SelectedMaterial buildMaterial(MaterialRequestBean materialRequestBean) {
        SelectedMaterial selectedMaterial = new SelectedMaterial();
        selectedMaterial.setMaterialId(materialRequestBean.getMaterialId() != null ? materialRequestBean.getMaterialId() : keyManagerUtil.getUniqueId());
        selectedMaterial.setPackageRoomId(materialRequestBean.getPackageRoomId());
        selectedMaterial.setMaterialName(materialRequestBean.getMaterialName());
        selectedMaterial.setMaterialCode(materialRequestBean.getMaterialCode());
        selectedMaterial.setMaterialUnitName(materialRequestBean.getMaterialUnitName());
        selectedMaterial.setMaterialUnitCode(QuoteUnitEnum.getMdmByName(materialRequestBean.getMaterialUnitCode()));
        selectedMaterial.setLimitQuantity(materialRequestBean.getMaterialLimit());
        Brand brand = buildBrand(materialRequestBean);
        selectedMaterial.getBrands().add(brand);
        return selectedMaterial;
    }

    /**
     * 删除主材数据
     */
    @LogAnnotation
    public ServiceResponse deleteMaterial(long materialId, String brandCode, List<String> productCodes) throws PackageException {
        String message;
        Criteria criteria = Criteria.where(CommonStaticConst.Package.MATERIAL_ID).is(materialId)
                .and(CommonStaticConst.Package.BRANDS_BRANDCODE).is(brandCode)
                .and(CommonStaticConst.Package.BRANDS_PRODUCT_PCODE).in(productCodes);
        Query query = new Query(criteria);
        try {
            SelectedMaterial material = mongoTemplate.findOne(query, SelectedMaterial.class);
            if (material == null) {
                return new ServiceResponse(true, "系统已经无该主材数据");
            }
            Optional<Brand> selectBrand = material.getBrands().parallelStream().filter(brand -> brandCode.equals(brand.getBrandCode())).findFirst();
            if (selectBrand.isPresent()) {
                Brand brand = selectBrand.get();
                List<Product> products = brand.getProduct();
                for (int i = 0; i < productCodes.size(); i++) {
                    for (int j = 0; j < products.size(); j++) {
                        if (productCodes.get(i).equals(products.get(j).getPCode())) {
                            products.remove(j);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(products)) {
                    material.getBrands().remove(brand);
                }
            }
            if (CollectionUtils.isEmpty(material.getBrands())) {
                mongoTemplate.remove(query, SelectedMaterial.class);
            } else {
                Update update = new Update();
                update.set(CommonStaticConst.Package.BRANDS, material.getBrands());
                mongoTemplate.updateMulti(query, update, SelectedMaterial.class);
            }
        } catch (Exception e) {
            message = String.format("删除主材发生异常{}", e);
            logger.error(message);
            throw new PackageException(message);
        }
        return new ServiceResponse(true, "删除主材成功");
    }
}
