package com.ecnu.paper.quotesystem.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.juran.core.exception.ParentException;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.enums.QuoteHouseEnum;
import com.juran.quote.bean.po.*;
import com.juran.quote.bean.po.Package;
import com.juran.quote.bean.request.PackageRequestBean;
import com.juran.quote.bean.request.PackageVersionRequestBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.GetDefaultPkgPriceRespBean;
import com.juran.quote.bean.response.PackageVersionResponseBean;
import com.juran.quote.bean.response.StoreRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.juran.quote.utils.CommonStaticConst.Common.ENABLE_STATUS;

@Service
public class PackageService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    QuoteConfig quoteConfig;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    KeyManagerUtil keyManagerUtil;

    public void insert(Package aPackage) throws ParentException {
    }

    public int delete(String id) {
        return 0;
    }

    public int update(Package aPackage) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, aPackage.getPackageId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        //构建更新
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(aPackage, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, Package.class);
        return writeResult.getN();
    }

    public Package getById(Long id) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, id);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, Package.class);
    }

    public Package queryPackageByCode(String code) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("packageCode", code);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, Package.class);
    }

    public Package queryPackageById(Long packageId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("packageId", packageId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, Package.class);
    }

    public List<Package> queryAllPackage() {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Common.STATUS, ENABLE_STATUS);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        return mongoTemplate.find(basicQuery, Package.class);
    }

    public List<Package> getAllPackagesWithFilter(Map<String, Object> args) {
        Query basicQuery = MDQueryUtil.getDBObjectByValues(args);
        return mongoTemplate.find(basicQuery, Package.class);
    }

    public List<PackageVersion> queryAllPackageVersion(String storeId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(CommonStaticConst.Common.STATUS, ENABLE_STATUS);
        basicDBObject.put("store", new BasicDBObject("$in", Lists.newArrayList(storeId)));
        Query basicQuery = MDQueryUtil.getDBObjectByValues(basicDBObject);
        List<PackageVersion> retList = mongoTemplate.find(basicQuery, PackageVersion.class);
        return CollectionUtils.isEmpty(retList)? new ArrayList<>():retList;
    }

    public List<Package> queryStoreAllPackage(String storeId) {
        //先查询所有的套餐
        List<Package> allPackage = queryAllPackage();
        //查询门店支持的所有的套餐版本
        List<PackageVersion> allPackageVersions = queryAllPackageVersion(storeId);

        List<Package> retPackage = new ArrayList<>();
        //如果没有查到直接返回null
        if (allPackage.isEmpty() || allPackageVersions.isEmpty()) {
            return retPackage;
        }
        //从所有套餐中过滤出门店支持的套餐
        for (PackageVersion pkgVersion : allPackageVersions) {
            if (pkgVersion.getPackageVersionId() > 5) {
                continue;
            }
            for (Package pkg : allPackage) {
                if (pkgVersion.getPackageId() != null && pkgVersion.getPackageId().equals(pkg.getPackageId())) {
                    retPackage.add(pkg);
                }
            }

        }
        return retPackage;
    }

    public List<Package> queryPackageByName(String name) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Common.STATUS, ENABLE_STATUS);
        List<String> likeList = Lists.newArrayList();
        if (!StringUtils.isEmpty(name)) {
            valuesMap.put("packageName", name);
            likeList.add("packageName");
        }
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, likeList);
        return mongoTemplate.find(basicQuery, Package.class);
    }

    public List<PackageVersionResponseBean> queryPkgVersion(String version,
                                                            Long packageId,
                                                            Long startTime,
                                                            Long endTime,
                                                            String region,
                                                            String store,
                                                            Long versionId) {
        Criteria criatira = new Criteria();
        if(!StringUtils.isEmpty(version)) criatira.and(CommonStaticConst.Package.VERSION).is(version);
        if(packageId != null) criatira.and(CommonStaticConst.Package.PACKAGE_ID).is(packageId);
        if(startTime != null) criatira.and(CommonStaticConst.Package.START_TIME).lte(new Date(startTime + 86400000));
        if(endTime != null) criatira.and(CommonStaticConst.Package.END_TIME).gte(new Date(endTime));
        if(!StringUtils.isEmpty(region)) criatira.and(CommonStaticConst.Package.REGION).is(region);
        if(!StringUtils.isEmpty(store)) criatira.and(CommonStaticConst.Package.STORE).is(store);
        if(versionId != null) criatira.and(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(versionId);
        Query basicQuery = new Query(criatira);

        List<PackageVersionResponseBean> responseList= new ArrayList<>();
        List<PackageVersion> versions = mongoTemplate.find(basicQuery, PackageVersion.class);
        if(!CollectionUtils.isEmpty(versions)){
            Map<String, Object> queryParams = Maps.newHashMap();
            responseList = versions.stream().map(v -> {
                queryParams.clear();
                queryParams.put("packageId", v.getPackageId());
                PackageVersionResponseBean responseBean = new PackageVersionResponseBean();
                List<Package> pkgs = getAllPackagesWithFilter(queryParams);
                responseBean.setVersion(v.getVersion());
                responseBean.setPackageName(StringUtils.isEmpty(pkgs)?"":pkgs.get(0).getPackageName());
                responseBean.setPackageVersionId(v.getPackageVersionId());
                responseBean.setStartTime(v.getStartTime().getTime());
                responseBean.setEndTime(v.getEndTime().getTime());
                responseBean.setRegion(v.getRegion());
                List<StoreRespBean> stores = new ArrayList<>();
                if(!CollectionUtils.isEmpty(v.getStores())){
                        stores =  v.getStores().stream().map(s-> {
                        StorePo sto = cacheUtil.getStore(s);
                        StoreRespBean storeRespBean = new StoreRespBean();
                        if(sto != null) {
                            BeanUtils.copyProperties(sto, storeRespBean);
                            return storeRespBean;
                        }
                        return null;
                    }).collect(Collectors.toList());
                }
                responseBean.setStores(stores);
                return responseBean;
            }).collect(Collectors.toList());
        }
        return responseList;
    }

    /**
     * 根据packageId查询packageVersion
     *
     * @param packageId
     * @return
     */
    public PackageVersion queryOnePkgVersionByPkgId(Long packageId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, packageId);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        return mongoTemplate.findOne(basicQuery, PackageVersion.class);
    }


    public PackageVersion queryOnePkgVersionByStore(PackageVersion queryBean, String store) {
        Date currentDate = new Date();
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(CommonStaticConst.Package.PACKAGE_ID, queryBean.getPackageId());
        basicDBObject.put("startTime", new BasicDBObject("$lt", currentDate));
        basicDBObject.put("endTime", new BasicDBObject("$gt", currentDate));
        basicDBObject.put("store", new BasicDBObject("$in", Lists.newArrayList(store)));
        basicDBObject.put("status", ENABLE_STATUS);
        BasicQuery basicQuery = new BasicQuery(basicDBObject);
        basicQuery.with(new Sort(Sort.Direction.DESC, "sort"));
        List<PackageVersion> pvList = mongoTemplate.find(basicQuery, PackageVersion.class);
        return CollectionUtils.isEmpty(pvList) ? null : pvList.get(0);
    }


    public PackageVersion queryOneEnablePkgVersion(Long packageId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, packageId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, PackageVersion.class);
    }

    public List<PackageVersion> queryVersionByPackageId(Long packageId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, packageId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.find(basicQuery, PackageVersion.class);
    }

    public int updatePackageVersion(PackageVersion updateBean) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, updateBean.getPackageId());
        valuesMap.put(CommonStaticConst.Package.VERSION, updateBean.getVersion());

        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(updateBean, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, PackageVersion.class);
        return writeResult.getN();
    }

    public List<PackageRoom> queryAllPackageRoom(Long packageVersionId, String houseType, String roomType) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        if (!StringUtils.isEmpty(houseType)) {
            valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, houseType);
        }
        if (!StringUtils.isEmpty(roomType)) {
            valuesMap.put("roomType", roomType);
        }
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        //排序
        basicQuery.with(new Sort(Sort.Direction.ASC, "sort"));
        return mongoTemplate.find(basicQuery, PackageRoom.class);
    }

    public List<PackageRoom> queryConstructRoom(Long packageVersionId, Long constructId, String houseType) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        if(constructId != null) valuesMap.put("selectedConstruct.cid", constructId);
        if(!StringUtils.isEmpty(houseType)) valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, houseType);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.find(basicQuery, PackageRoom.class);
    }


    public List<PackageRoom> queryAllPackageRoom(Long packageVersionId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        //排序
        basicQuery.with(new Sort(Sort.Direction.ASC, "sort"));
        return mongoTemplate.find(basicQuery, PackageRoom.class);
    }

    public PackagePrice queryOnePackagePrice(Long packageVersionId, String houseType) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, houseType);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, PackagePrice.class);
    }

    public List<PackagePrice> queryPackagePriceByVersionId(Long packageVersionId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.find(basicQuery, PackagePrice.class);
    }

    public CommonRespBean createPackage(PackageRequestBean pkg) throws ParentException {
        CommonRespBean response;
        String message;
        Package target = new Package();
        BeanUtils.copyProperties(pkg, target);
        target.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
        if(pkg.getPackageId() == null) target.setPackageId(keyManagerUtil.getUniqueId());

        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, target.getPackageId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        try {
            if (mongoTemplate.exists(basicQuery, Package.class)) {
                message = String.format("套餐id:%d已经存在，无需重复创建", pkg.getPackageId());
                logger.info(message);
                response = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, message));
                return response;
            }
            mongoTemplate.insert(target);
        } catch (RuntimeException e) {
            logger.error("创建套餐异常 %s", e);
            throw new PackageException("创建套餐异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,"创建套餐成功"));
    }

    public Boolean removePackage(Package pkg) throws PackageException {
        if (backUpPackage(pkg)) {
            logger.info("套餐备份完成 id{}", pkg.getPackageId());
        }

        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, pkg.getPackageId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);

        try {
            WriteResult result = mongoTemplate.remove(basicQuery, Package.class);
            if (result.getN() == 0) {
                return false;
            }
        } catch (RuntimeException e) {
            String message = String.format("删除套餐操作异常 , 异常信息：%s", e);
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    public CommonRespBean createPackageVersion(PackageVersionRequestBean packageVersion) throws ParentException {
        String message;
        PackageVersion version = new PackageVersion();
        BeanUtils.copyProperties(packageVersion, version);
        version.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
        version.setUpdateTime(new Date());
        version.setStartTime(new Date(packageVersion.getStartTime()));
        version.setEndTime(new Date(packageVersion.getEndTime()));
        if(packageVersion.getPackageVersionId() == null) version.setPackageVersionId(keyManagerUtil.getUniqueId());
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, version.getPackageVersionId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        try {
            if (mongoTemplate.exists(basicQuery, PackageVersion.class)) {
                message = String.format("套餐版本%d已经存在，无需重复创建", packageVersion.getPackageVersionId());
                logger.info(message);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, message));
            }
            mongoTemplate.insert(version);
        } catch (RuntimeException e) {
            message = String.format("创建套餐版本操作异常 , 异常信息：%s", e.getMessage());
            logger.error(message);
            throw new PackageException("创建套餐版本操作异常");
        }
        message = String.format("套餐版本%d创建成功", packageVersion.getPackageVersionId());
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message));
    }


    public List<PackageBag> queryPackageBagByVersionId(Long packageVersionId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        return mongoTemplate.find(new BasicQuery(basicDBObject), PackageBag.class);
    }

    public List<PackageBag> queryPackageBagByVersionIdHouseType(Long packageVersionId, String houseType) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        basicDBObject.put(CommonStaticConst.Package.HOUSE_TYPE, houseType);
        return mongoTemplate.find(new BasicQuery(basicDBObject), PackageBag.class);
    }

    public List<BagConstruct> queryBagConstructByBagId(Long bagId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("bagId", bagId);
        return mongoTemplate.find(new BasicQuery(basicDBObject), BagConstruct.class);
    }

    public PackageBag queryPackageBagById(Long bagId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("bagId", bagId);
        return mongoTemplate.findOne(new BasicQuery(basicDBObject), PackageBag.class);
    }

    public PackageRoom queryPackageRoomByParams(PackageRoom packageRoom) {
        if (CollectionUtils.isEmpty(packageRoom.getSelectedConstruct())) {
            packageRoom.setSelectedConstruct(null);
        }
        if (CollectionUtils.isEmpty(packageRoom.getSelectedMaterial())) {
            packageRoom.setSelectedMaterial(null);
        }
        Map<String, Object> valuesMap = ClassUtil.getClassFieldAndValue(packageRoom, Boolean.FALSE);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, PackageRoom.class);
    }

    public int updatePackageRoom(PackageRoom packageRoom) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageRoom.getPackageVersionId());
        valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, packageRoom.getHouseType());
        valuesMap.put(CommonStaticConst.Package.ROOM_TYPE, packageRoom.getRoomType());

        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(packageRoom, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, PackageRoom.class);
        return writeResult.getN();
    }

    public Long getDefaultPackageId(Boolean houseState) {
        if (houseState) {
            return quoteConfig.getDefaultNewPackageId();
        }
        return quoteConfig.getDefaultOldPackageId();
    }

    public List<GetDefaultPkgPriceRespBean> getDefaultPkgPrice() {
        List<GetDefaultPkgPriceRespBean> list = new ArrayList<>();
        PackageVersion packageVersion = queryOneEnablePkgVersion(getDefaultPackageId(true));
        if (packageVersion != null) {
            List<PackagePrice> priceList = queryPackagePriceByVersionId(packageVersion.getPackageVersionId());
            for (PackagePrice price : priceList) {
                GetDefaultPkgPriceRespBean bean = new GetDefaultPkgPriceRespBean();
                try {
                    QuoteHouseEnum quoteHouseEnum = Enum.valueOf(QuoteHouseEnum.class, price.getHouseType().toUpperCase());
                    bean.setHouseType(quoteHouseEnum.getName());
                    bean.setBasePrice(price.getBaseAmount());
                    bean.setInnerArea(price.getBaseArea());
                    list.add(bean);
                } catch (Exception e) {

                }
            }
        }
        return list;
    }

    public List<Brand> queryAllBrandByPackageId(String packageId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("product.pidWithPackageId", packageId);
        List<String> likeList = Lists.newArrayList();
        likeList.add("product.pidWithPackageId");
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, likeList);
        return mongoTemplate.find(basicQuery, Brand.class);
    }

    public Set<String> getAllProductByPackageId(Long packageId) {
        List<Brand> brandList = this.queryAllBrandByPackageId("_" + packageId);
        Set<String> sets = Sets.newHashSet();
        for (Brand brand : brandList) {
            List<Product> productList = brand.getProduct();
            for (Product p : productList) {
                String res = p.getPidWithPackageId();
                sets.add(res);
            }
        }
        return sets;
    }

    @LogAnnotation
    public boolean removePackageVersionByPackage(Long packageId) throws PackageException {
        PackageVersion version = getPackageVersionByPackage(packageId);
        if (version == null) {
            logger.error("套餐版本之id{}不存在", packageId);
            return true;
        }
        try {
            removePackageVersion(version.getPackageVersionId());
        } catch (PackageException e) {
            throw  new PackageException("删除套餐版本异常");
        }
        return true;
    }

    @LogAnnotation
    public PackageVersion getPackageVersionByPackage(Long packageId) {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_ID).is(packageId));
        Query query = new Query(criatira);
        PackageVersion version = mongoTemplate.findOne(query, PackageVersion.class);
        return version;
    }

    @LogAnnotation
    public CommonRespBean removePackageVersion(Long pkgVersionId) throws PackageException {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(pkgVersionId));
        Query query = new Query(criatira);

        PackageVersion version = mongoTemplate.findOne(query, PackageVersion.class);
        String message;
        if (version == null) {
            message = String.format("套餐版本id:%d 已经删除或者不存在", pkgVersionId);
            logger.info(message);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, message));
        }

        try {
            backPckageVersion(version);
            WriteResult result = mongoTemplate.remove(query, PackageVersion.class);
            if (result.getN() < 1) {
                message = String.format("套餐版本id:%d 删除失败", pkgVersionId);
                new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL, message));
            }
        } catch (PackageException e) {
            message = String.format("删除套餐版本异常%s", e.getMessage());
            logger.info(message);
            throw e;
        }
        message = String.format("套餐版本id:%d 删除成功", pkgVersionId);
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message));
    }

    private boolean backPckageVersion(PackageVersion version) throws PackageException {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(version.getPackageVersionId()));
        Query query = new Query(criatira);
        String message;
        try {
            if (mongoTemplate.exists(query, PackageVersionBackup.class)) {
                message = String.format("套餐版本id:%d 已经删除或者不存在", version.getPackageVersionId());
                logger.info(message);
                return true;
            }
            PackageVersionBackup versionBackup = new PackageVersionBackup();
            BeanUtils.copyProperties(version, versionBackup);
            versionBackup.setRemoveDate(new Date());
            mongoTemplate.insert(versionBackup);
        } catch (BeansException e) {
            message = String.format("备份套餐版本出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        } catch (RuntimeException e) {
            message = String.format("备份套餐版本出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    public CommonRespBean removePackage(Long pkgId) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, pkgId);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Package pkg = mongoTemplate.findOne(basicQuery, Package.class);
        String message;
        if (pkg == null) {
            message = String.format("套餐 id:%d 已经不存在或者已经删除", pkgId);
            logger.info(message);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message));
        }

        try {
            if (!backUpPackage(pkg)) {
                message = String.format("备份套餐%d失败", pkg.getPackageId());
                logger.error(message);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_BACKUP_ERROR, message));
            }
            WriteResult result = mongoTemplate.remove(basicQuery, Package.class);
            if (result.getN() == 0) {
                message = String.format("删除套餐%d失败", pkg.getPackageId());
                logger.error(message);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL, message));
            }
        } catch (RuntimeException e) {
            message = String.format("删除套餐操作异常 , 异常信息：%s", e);
            logger.error(message);
            throw new PackageException("删除套餐操作异常");
        }
        message = String.format("删除套餐%d成功", pkg.getPackageId());
        logger.info(message);
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message));
    }

    private Boolean backUpPackage(Package pkg) throws PackageException {
        try {
            Map<String, Object> valuesMap = Maps.newHashMap();
            valuesMap.put(CommonStaticConst.Package.PACKAGE_ID, pkg.getPackageId());
            Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
            if (mongoTemplate.exists(basicQuery, PackageBackup.class)) {
                logger.error("套餐前期已经备份 id：{}", pkg.getPackageId());
                return true;
            }


            PackageBackup target = new PackageBackup();
            BeanUtils.copyProperties(pkg, target);
            target.setStatus(CommonStaticConst.Common.DISABLE_STATUS);
            target.setRemoveTime(new Date());

            mongoTemplate.insert(target);
        } catch (RuntimeException e) {
            String message = String.format("备份套餐id：%d操作异常 , 异常信息：%s", pkg.getPackageId(), e);
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    @LogAnnotation
    public ServiceResponse removePackageByPackageVersion(Long pkgVersionId) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, pkgVersionId);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Package pkg = mongoTemplate.findOne(basicQuery, Package.class);
        String message;
        if (pkg == null) {
            message = String.format("套餐已经不存在或者已经删除");
            logger.info(message);
            return new ServiceResponse(true,"套餐已经不存在或者已经删除");
        }
        try {
            if (!backUpPackage(pkg)) {
                message = String.format("备份套餐%d失败", pkg.getPackageId());
                logger.error(message);
                new ServiceResponse(false,message);
            }
            WriteResult result = mongoTemplate.remove(basicQuery, Package.class);
            if (result.getN() == 0) {
                message = String.format("删除套餐%d失败", pkg.getPackageId());
                logger.error(message);
                new ServiceResponse(false,message);
            }
        } catch (RuntimeException e) {
            message = String.format("删除套餐操作异常 , 异常信息：%s", e);
            logger.error(message);
            throw new PackageException("删除套餐操作异常 ");
        }
        message = String.format("删除套餐%d成功", pkg.getPackageId());
        logger.info(message);
        return new ServiceResponse(true,message);
    }

    public List<StorePo> queryStores(String region) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        if (!StringUtils.isEmpty(region)) {
            valuesMap.put("region", region);
        }
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "region"));
        basicQuery.with(sort);
        List<StorePo> list = mongoTemplate.find(basicQuery, StorePo.class);
        return list;
    }
}
