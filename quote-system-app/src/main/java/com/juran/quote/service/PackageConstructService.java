package com.juran.quote.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.juran.core.exception.ParentException;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.dto.BindSelectedMaterialDto;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.enums.BatchTemplateFileds;
import com.juran.quote.bean.enums.BindConstructByDefault;
import com.juran.quote.bean.enums.ConstructPriceTemplateEnum;
import com.juran.quote.bean.enums.OSType;
import com.juran.quote.bean.enums.StatusEnum;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.*;
import com.juran.quote.bean.po.Dictionary;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.*;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PackageConstructService extends BaseService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PackageService packageService;

    @Autowired
    private KeyManagerUtil keyManagerUtil;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ConstructService constructService;

    @Autowired
    private PropertiesUtils propertiesUtils;

    @Autowired
    private DecorationTypeService decorationTypeService;

    @Autowired
    private QuoteTypeService quoteTypeService;

    @Autowired
    private QuoteVersionService quoteVersionService;

    @Autowired
    private PackageRoomService packageRoomService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    CacheUtil cacheUtil;
    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @LogAnnotation
    public CommonRespBean removePackageConstruct(String pConstructIds) throws PackageException {
        List<Long> ids = new ArrayList<>();
        for (String id : pConstructIds.split(",")) {
            ids.add(Long.valueOf(id));
        }
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PKG_CONSTRUCT_ID).in(ids));
        Query query = new Query(criatira);

        try {
            mongoTemplate.findAllAndRemove(query, PackageConstruct.class);
        } catch (RuntimeException e) {
            logger.error("删除套餐施工项出现异常{}", e.getMessage());
            throw new PackageException("删除套餐施工项出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除套餐施工项成功"));
    }

    @LogAnnotation
    public Boolean removePackageConstructByPackage(Long packageId) throws PackageException {
        PackageVersion version = packageService.getPackageVersionByPackage(packageId);
        if (version != null) {
            try {
                removePackageConstructByPkgVersion(version.getPackageVersionId());
            } catch (PackageException e) {
                throw new PackageException("删除套餐施工项异常");
            }
        }
        return true;
    }

    @LogAnnotation
    public ServiceResponse removePackageConstructByPkgVersion(Long pkgVersionId) throws PackageException {
        List<PackageConstruct> pkgConstructList = getConstructForPackage(pkgVersionId);
        if (pkgConstructList == null) {
            return new ServiceResponse(true, "套餐施工项已删除，无需重复操作");
        }
        for (PackageConstruct construct : pkgConstructList) {
            backupPackageConstruct(construct);
        }
        try {
            Criteria criatira = new Criteria();
            criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).in(pkgVersionId));
            Query query = new Query(criatira);
            mongoTemplate.findAllAndRemove(query, PackageConstruct.class);
        } catch (RuntimeException e) {
            String message = String.format("删除套餐施工项出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException("删除套餐施工项出现异常");
        }
        return new ServiceResponse(true, "套餐施工项删除成功");
    }

    private Boolean backupPackageConstruct(PackageConstruct construct) throws PackageException {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PKG_CONSTRUCT_ID).in(construct.getPackageVersionId()));
        Query query = new Query(criatira);
        String message;
        try {
            if (mongoTemplate.exists(query, PackageConstructBackup.class)) {
                logger.info("套餐施工项id{}已经备份无需删除", construct.getPackageConstructId());
                return true;
            }
            PackageConstructBackup backup = new PackageConstructBackup();
            BeanUtils.copyProperties(construct, backup);
            backup.setRemoveDate(new Date());
            mongoTemplate.insert(backup);
        } catch (BeansException e) {
            message = String.format("备份套餐施工项出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        } catch (RuntimeException e) {
            message = String.format("备份套餐施工项出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    /**
     * 新增施工项
     *
     * @param source
     * @throws PackageException
     */
    @LogAnnotation
    public CommonRespBean insertConstruct(ConstructBean source) throws PackageException {
        CommonRespBean checkResult = checkConstructCode(source.getDecorationCompany(), source.getConstructCode());
        if (checkResult.getStatus().getCode().equals(CommonRespBean.Status.DATA_EXISTING)) {
            //updateConstructFromConstructBean(source);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, String.format("施工项%s已经存在", source.getConstructCode())));
        }
        try {
            ConstructPo construct = new ConstructPo();
            BeanUtils.copyProperties(source, construct);
            construct.setConstructId(source.getConstructId() != null ? source.getConstructId() : keyManagerUtil.getUniqueId());
            construct.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            construct.setCreateTime(new Date());
            construct.setUpdateTime(construct.getCreateTime());
            //默认给自定义项目
            construct.setConstructCategory(CommonStaticConst.Construct.DEFAULT_CONSTRUCT_CATEGORY);
            mongoTemplate.insert(construct);
            cacheUtil.initIndividualConstruct();
        } catch (BeansException e) {
            logger.error("创建施工项出现异常{}", e.getMessage());
            throw new PackageException("创建施工项出现异常");
        } catch (RuntimeException e) {
            logger.error("创建施工项出现异常{}", e.getMessage());
            throw new PackageException("创建施工项出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "添加施工项成功"));
    }


    private int updateConstructFromConstructBean(ConstructBean source) throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (source.getConstructId() != null) {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_ID, source.getConstructId());
            } else {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, source.getConstructCode());
            }
            basicDBObject.put(CommonStaticConst.Construct.STATUS, CommonStaticConst.Common.ENABLE_STATUS);

            Update update = new Update();
            update.set(CommonStaticConst.Construct.CONSTRUCT_ITEM, source.getConstructItem());
            update.set(CommonStaticConst.Construct.CONSTRUCT_NAME, source.getConstructName());
            update.set(CommonStaticConst.Construct.UNIT_CODE, source.getUnitCode());
            update.set(CommonStaticConst.Construct.DESC, source.getDesc());
            update.set(CommonStaticConst.Construct.ASSIT_SPEC, source.getAssitSpec());
            update.set(CommonStaticConst.Construct.STANDARD, source.getStandard());
            update.set(CommonStaticConst.Construct.SOURCE_OF_STANDARD, source.getSourceOfStandard());
            update.set(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());
            update.set(CommonStaticConst.Construct.REMARK, source.getRemark());
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, source.getUpdateBy());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, ConstructPo.class);
            if (writeResult.getN() > 0) {
                logger.info("更新施工项成功：{}", source.toString());
                cacheUtil.initIndividualConstruct();
            } else {
                logger.info("更新施工项失败：{}", source.toString());
            }
            return writeResult.getN();
        } catch (Exception e) {
            logger.error("更新施工项成功异常{}", e.getMessage());
            throw new PackageException("更新施工项成功异常");
        }
    }

    /**
     * 逻辑删除施工项
     *
     * @param idList
     * @return
     * @throws PackageException
     */
    @LogAnnotation
    public CommonRespBean deleteConstruct(List<Long> idList, String updateBy) throws PackageException {
        try {
            Criteria criteria = Criteria.where(CommonStaticConst.Construct.CONSTRUCT_ID).in(idList);
            criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
            //获取施工项code列表
            List<ConstructPo> constructs = mongoTemplate.find(new Query(criteria), ConstructPo.class);
            if(CollectionUtils.isEmpty(constructs)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "系统不存在要删除的数据"));
            }
            String decorationCompany = constructs.get(0).getDecorationCompany();
            List<String> constructCodes = constructs.stream().map(ConstructPo::getConstructCode).collect(Collectors.toList());

            //验证是否有正在使用的施工项报价
            Criteria constructCodeParam = Criteria.where(CommonStaticConst.Construct.CONSTRUCT_CODE).in(constructCodes);
            constructCodeParam.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
            constructCodeParam.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
            Boolean hasConstructPriceUsed = mongoTemplate.exists(new Query(constructCodeParam), ConstructPricePo.class);
            if(hasConstructPriceUsed){
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "存在正在使用的施工项报价，无法执行删除操作"));
            }
            //验证是否有正在使用的施工项带出关系

            Boolean hasConstructRelationship = mongoTemplate.exists(new Query(criteria), ConstructRelationship.class);
            if(hasConstructRelationship){
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "存在正在使用的施工项带出关系，无法执行删除操作"));
            }

            //验证是否有正在使用的施工项计算公式
            Boolean hasConstructCalExpression = mongoTemplate.exists(new Query(criteria), CalculatorExpression.class);
            if(hasConstructCalExpression){
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "存在正在使用的施工项计算公式，无法执行删除操作"));
            }

            Update update = new Update();
            update.set(CommonStaticConst.Construct.STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            update.set(CommonStaticConst.Common.UPDATE_BY, updateBy);
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            WriteResult writeResult = mongoTemplate.updateMulti(new Query(criteria), update, ConstructPo.class);
            cacheUtil.initIndividualConstruct();
            if (writeResult != null) {
                logger.info("共删除{}条记录", writeResult.getN());
            }
        } catch (BeansException e) {
            logger.error("删除施工项出现异常%s", e.getMessage());
            throw new PackageException("删除施工项出现异常");
        } catch (Exception e) {
            logger.error("删除施工项出现异常:%s", e);
            throw new PackageException("删除施工项出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除施工项成功"));
    }


    @LogAnnotation
    public CommonRespBean createPackageConstruct(PackageConstructRequestBean source) throws PackageException {
        HashMap<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, source.getPackageVersionId());
        paramsMap.put(CommonStaticConst.Package.HOUSE_TYPE, source.getHouseType());
        paramsMap.put(CommonStaticConst.Package.CONSTRUCT_ID, source.getConstructId());
        Query query = MDQueryUtil.getDBObjectByValues(paramsMap);
        String message = "";

        try {
            if (mongoTemplate.exists(query, PackageConstruct.class)) {
                message = String.format("套餐施工项id %s，套餐版本%s，户型%s 已存在，无需创建",
                        source.getConstructId(),
                        source.getPackageVersionId(),
                        source.getHouseType());
                logger.info(message);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, message));
            }

            PackageConstruct packageConstruct = new PackageConstruct();
            BeanUtils.copyProperties(source, packageConstruct);
            packageConstruct.setPackageConstructId(keyManagerUtil.getUniqueId());
            mongoTemplate.insert(packageConstruct);
        } catch (RuntimeException e) {
            logger.error("创建套餐施工项出现异常%s", e.getMessage());
            throw new PackageException("创建套餐施工项出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "创建套餐施工项成功"));
    }

    public ServiceResponse<List<ConstructRespBean>> getPackageConstructByPkgVersion(Long pkgVersionId) throws PackageException {
        List<PackageConstruct> packageConstructs = getConstructForPackage(pkgVersionId);
        Map<String, PackageConstruct> packageConstructMap = Maps.newHashMap();
        for (PackageConstruct pc : packageConstructs) {
            packageConstructMap.put(pc.getConstructId().toString(), pc);
        }
        List<Construct> constructList = Lists.newArrayList();
        Map<String, Construct> constructMap = cacheUtil.getAllConstructMap();

        for (String id : packageConstructMap.keySet()) {
            Construct c = constructMap.get(id);
            if (null != c) {
                constructList.add(c);
            }
        }
        //返回值
        List<ConstructRespBean> resList = Lists.newArrayList();
        for (Construct c : constructList) {
            ConstructRespBean bean = new ConstructRespBean();
            BeanUtils.copyProperties(c, bean);
            PackageConstruct packageConstruct = packageConstructMap.get(c.getConstructId().toString());
            bean.setPackageConstructId(packageConstruct.getPackageConstructId());
            bean.setLimit(packageConstruct.getLimitQuantity());
            resList.add(bean);
        }
        String message = String.format("套餐版本%s获取%d个施工项",
                pkgVersionId.toString(), resList == null ? 0 : resList.size());
        return new ServiceResponse<List<ConstructRespBean>>(true, message, resList);
    }

    private List<PackageConstruct> getConstructForPackage(Long pkgVersion) throws PackageException {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).in(pkgVersion));
        Query query = new Query(criatira);

        try {
            return mongoTemplate.find(query, PackageConstruct.class);
        } catch (Exception e) {
            throw new PackageException("根据套餐版本获取施工项异常");
        }
    }

    public CommonRespBean<Map<String, Object>> getPkgConstructRelationship(Long pkgConstructId, Long version, String houseType) {
        Set<String> relatedRooms = getRelatedRoomsForPkgConstruct(version, pkgConstructId, houseType);
        Set<Map<String, Object>> relatedMaterials = getRelatedMaterialForPkgConstuct(version, pkgConstructId, houseType);
        Map<String, Object> result = Maps.newHashMap();
        result.put("success", Boolean.TRUE);
        result.put(CommonStaticConst.Construct.TYPE_ROOM, relatedRooms);
        result.put(CommonStaticConst.Construct.TYPE_MATERIAL, relatedMaterials);
        return new CommonRespBean<>(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "成功"), result);
    }

    public PackageConstruct getPackageConstructById(Long pkgConstructId) {
        HashMap<String, Object> params = Maps.newHashMap();
        params.put(CommonStaticConst.Package.PKG_CONSTRUCT_ID, pkgConstructId);
        Query query = MDQueryUtil.getDBObjectByValues(params);
        return mongoTemplate.findOne(query, PackageConstruct.class);
    }

    private Set<String> getRelatedRoomsForPkgConstruct(Long pkgVersionId, Long constructId, String houseType) {
        //空间
        List<PackageRoom> packageRoomList = packageService.queryConstructRoom(Long.valueOf(pkgVersionId), Long.valueOf(constructId), houseType);
        Set<String> roomSet = packageRoomList.stream().map(PackageRoom::getRoomType).collect(Collectors.toSet());
        List<PackageRoom> allPackageRoomList = packageService.queryConstructRoom(Long.valueOf(pkgVersionId), null, houseType);
        Map<String, List<PackageRoom>> groupRoom = allPackageRoomList.parallelStream().collect(Collectors.groupingBy(PackageRoom::getHouseType));
        if (org.springframework.util.StringUtils.isEmpty(houseType)) {
            for (String key : groupRoom.keySet()) {
                List<String> packageRooms = groupRoom.get(key).stream().map(PackageRoom::getRoomType).collect(Collectors.toList());
                roomSet = roomSet.stream().filter(roomType -> {
                    Optional<PackageRoom> first = groupRoom.get(key).stream().filter(r -> r.getRoomType().equals(roomType)).findFirst();
                    Set<Long> cIds = Sets.newHashSet();
                    if (first.isPresent()) {
                        cIds = first.get().getSelectedConstruct().stream().map(c -> c.getCid()).collect(Collectors.toSet());
                    }
                    if (!packageRooms.contains(roomType)) {
                        return true;
                    } else if (cIds.contains(Long.valueOf(constructId))) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toSet());
            }
        }
//        Set<String> retSet = Sets.newHashSet();
//                List<PackageRoom> rooms = packageService.queryConstructRoom(pkgVersionId, constructId, houseType);
//        if(!CollectionUtils.isEmpty(rooms)){
//            retSet = rooms.stream().map(PackageRoom::getRoomType).collect(Collectors.toSet());
//        }
        return roomSet;
    }

    private Set<Map<String, Object>> getRelatedMaterialForPkgConstuct(Long pkgVersionId, Long constructId, String houseType) {
        //主材
        List<PackageRoom> packageRoomMaterialList = packageService.queryAllPackageRoom(pkgVersionId, houseType, null);

        Set<Map<String, Object>> resSet = filterMaterial(packageRoomMaterialList, constructId);

        if (org.springframework.util.StringUtils.isEmpty(houseType)) {
            Map<String, List<PackageRoom>> groupRoomMaterial = packageRoomMaterialList.parallelStream().collect(Collectors.groupingBy(PackageRoom::getHouseType));
            for (String key : groupRoomMaterial.keySet()) {
                List<PackageRoom> packageRooms = groupRoomMaterial.get(key);
                Set<Map<String, Object>> roomResSet = filterMaterial(packageRooms, constructId);
                resSet = resSet.stream().filter(stringStringMap -> {
                    boolean flag = false;
                    for (Map m : roomResSet) {
                        if (m.get("code").equals(stringStringMap.get("code"))) {
                            flag = true;
                            break;
                        }
                    }
                    return flag;
                }).collect(Collectors.toSet());
            }
        }
        return resSet;
    }

    public Set<Map<String, Object>> filterMaterial(List<PackageRoom> packageRoomMaterialList, Long constructId) {
        List<Long> materialIdList = Lists.newArrayList();
        for (PackageRoom pr : packageRoomMaterialList) {
            materialIdList.addAll(pr.getSelectedMaterial());
        }
        //返回的施工项-主材
        Set<Map<String, Object>> resSet = Sets.newHashSet();
        //版本下所有的主材
        List<SelectedMaterial> materialList = materialService.queryMaterialList(materialIdList);
        logger.info("版本下的所有主材个数:{}", materialList.size());
        for (SelectedMaterial sm : materialList) {
            List<SelectedConstruct> constructList = sm.getSelectedConstruct();
            Integer bindDefault = 0;
            for (SelectedConstruct sc : constructList) {
                if (constructId.longValue() == sc.getCid().longValue()) {
                    if (sc.getCid().equals(constructId)) {
                        bindDefault = sc.getBindByDefault();
                    }
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("code", sm.getMaterialCode());
                    map.put("name", sm.getMaterialName());
                    map.put("bindByDefault", bindDefault == null ? 0 : bindDefault);
                    resSet.add(map);
                    break;
                }
            }
        }
        return resSet;
    }


    private void relateConstructWithMaterial(List<BindSelectedMaterialDto> packageMaterials, Long pkgVersionId, Long constructId) {
        for (BindSelectedMaterialDto bindMaterials : packageMaterials) {
            for (SelectedMaterial material : bindMaterials.getMaterialList()) {
                List<SelectedConstruct> materialConstruct = material.getSelectedConstruct();
                if (!CollectionUtils.isEmpty(materialConstruct)) {
                    Optional<SelectedConstruct> hasSpecificConstruct = materialConstruct
                            .stream()
                            .filter(c -> c.getCid().equals(constructId) && bindMaterials.getBindByDefault().equals(c.getBindByDefault()))
                            .findFirst();
                    if (hasSpecificConstruct.isPresent()) {
                        continue;
                    }
                    Optional<SelectedConstruct> changeDefault = materialConstruct
                            .stream()
                            .filter(c -> c.getCid().equals(constructId) && !bindMaterials.getBindByDefault().equals(c.getBindByDefault()))
                            .findFirst();
                    if (changeDefault.isPresent()) {
                        changeDefault.get().setBindByDefault(bindMaterials.getBindByDefault());
                        materialService.update(material);
                        continue;
                    }
                }

                SelectedConstruct construct = new SelectedConstruct();
                construct.setCid(constructId);
                construct.setRef(CommonStaticConst.Construct.TYPE_MATERIAL);
                construct.setUnitPrice(cacheUtil.getCachedConstruct(String.valueOf(constructId)).getCustomerPrice());
                construct.setLimit(this.getConstructLimit(pkgVersionId, constructId, null));
                construct.setBindByDefault(bindMaterials.getBindByDefault());
                materialConstruct.add(construct);
                material.setSelectedConstruct(materialConstruct);
                materialService.update(material);
            }
        }
    }

    private void decoubpleConstructWithMaterial(List<SelectedMaterial> packageMaterial, Long constructId) {
        List<SelectedMaterial> materialsWithSelectedConstruct = packageMaterial.stream().filter(m -> {
            List<SelectedConstruct> constructs = m.getSelectedConstruct();
            for (SelectedConstruct construct : constructs) {
                if (construct.getCid().equals(constructId)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        for (SelectedMaterial material : materialsWithSelectedConstruct) {
            List<SelectedConstruct> filterSelectedConstructs = material.getSelectedConstruct()
                    .stream()
                    .filter(construct -> !construct.getCid().equals(constructId))
                    .collect(Collectors.toList());
            material.setSelectedConstruct(filterSelectedConstructs);
            updateMaterial(material);
        }
    }

    public int updateMaterial(SelectedMaterial selectedMaterial) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("materialId", selectedMaterial.getMaterialId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(selectedMaterial, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, SelectedMaterial.class);
        return writeResult.getN();
    }

    private void decoupleConstrcutWithRoom(List<String> rooms, Long pkgVersionId, Long constructId, String houseType) {
        if (CollectionUtils.isEmpty(rooms)) {
            return;
        }
        Criteria criatira = new Criteria();
        criatira.and(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(pkgVersionId);
        criatira.and(CommonStaticConst.Package.ROOM_TYPE).in(rooms);
        criatira.and(CommonStaticConst.Package.SELECTED_CONSTRUCT_ID).is(constructId);
        if (!StringUtils.isEmpty(houseType)) criatira.and(CommonStaticConst.Package.HOUSE_TYPE).is(houseType);
        Query query = new Query(criatira);
        List<PackageRoom> packageRooms = mongoTemplate.find(query, PackageRoom.class);
        if (!CollectionUtils.isEmpty(packageRooms)) {
            for (PackageRoom room : packageRooms) {
                List<SelectedConstruct> filterSelectedConstructs = room.getSelectedConstruct()
                        .stream()
                        .filter(construct -> !construct.getCid().equals(constructId))
                        .collect(Collectors.toList());
                room.setSelectedConstruct(filterSelectedConstructs);

                packageService.updatePackageRoom(room);
            }
        }
    }

    private void relateConstructWithRoom(List<String> rooms, Long pkgVersionId, Long constructId, String houseType) {
        if (CollectionUtils.isEmpty(rooms)) {
            return;
        }
        Criteria criatira = new Criteria();
        if (pkgVersionId != null) criatira.and(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(pkgVersionId);
        criatira.and(CommonStaticConst.Package.ROOM_TYPE).in(rooms);
        if (constructId != null) criatira.and(CommonStaticConst.Package.SELECTED_CONSTRUCT_ID).ne(constructId);
        if (!StringUtils.isEmpty(houseType)) criatira.and(CommonStaticConst.Package.HOUSE_TYPE).is(houseType);
        Query query = new Query(criatira);

        List<PackageRoom> packageRooms = mongoTemplate.find(query, PackageRoom.class);
        if (!CollectionUtils.isEmpty(packageRooms)) {
            for (PackageRoom room : packageRooms) {
                List<SelectedConstruct> selectedConstructs = room.getSelectedConstruct();
//                if(selectedConstructs
//                        .stream()
//                        .filter(c -> c.getCid().equals(constructId))
//                        .findFirst()
//                        .isPresent()){
//                    continue;
//                }
                SelectedConstruct construct = new SelectedConstruct();
                construct.setCid(constructId);
                construct.setRef(CommonStaticConst.Construct.TYPE_ROOM);
                construct.setUnitPrice(cacheUtil.getCachedConstruct(String.valueOf(constructId)).getCustomerPrice());
                construct.setLimit(this.getConstructLimit(pkgVersionId, constructId, room.getRoomType()));
                construct.setBindByDefault(BindConstructByDefault.YES.getValue());
                selectedConstructs.add(construct);
                packageService.updatePackageRoom(room);
            }
        }
    }

    private BigDecimal getConstructLimit(Long packageVersionId, Long constructId, String houseType) {
        PackageConstruct packageConstruct = null;
        if (houseType == null) {
            packageConstruct = constructService.queryPackageConstructByVersionIdAndConstructId(packageVersionId, constructId);
        } else {
            packageConstruct = querySinglePackageConstruct(packageVersionId, constructId, houseType);
        }
        if (null != packageConstruct) {
            return packageConstruct.getLimitQuantity();
        }
        logger.warn("警告!查询套餐版本为:{},施工项为:{}的限量值,返回限量数据为0.", packageVersionId, constructId);
        return BigDecimal.ZERO;
    }

    private PackageConstruct querySinglePackageConstruct(Long packageVersionId, Long constructId, String houseType) {
        HashMap<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        paramsMap.put(CommonStaticConst.Package.HOUSE_TYPE, houseType);
        paramsMap.put(CommonStaticConst.Package.CONSTRUCT_ID, constructId);
        Query query = MDQueryUtil.getDBObjectByValues(paramsMap);
        //排序
        query.with(new Sort(Sort.Direction.DESC, "limitQuantity"));
        PackageConstruct pkgConstruct = mongoTemplate.findOne(query, PackageConstruct.class);
        return pkgConstruct;
    }

    @LogAnnotation
    public ServiceResponse createBagConstruct(BagConstructRequestBean requestBean) throws PackageException {
        HashMap<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put(CommonStaticConst.Package.CONSTRUCT_ID, requestBean.getConstructId());
        paramsMap.put(CommonStaticConst.Package.PKG_BAG_ID, requestBean.getBagId());
        Query query = MDQueryUtil.getDBObjectByValues(paramsMap);
        String message;
        if (mongoTemplate.exists(query, BagConstruct.class)) {
            message = String.format("礼包%s的施工项%s已存在， 无需创建",
                    requestBean.getBagId().toString(),
                    requestBean.getConstructId().toString());
            return new ServiceResponse(true, message);
        }

        try {
            BagConstruct bagConstruct = new BagConstruct();
            BeanUtils.copyProperties(requestBean, bagConstruct);
            mongoTemplate.insert(bagConstruct);
        } catch (RuntimeException e) {
            message = "创建礼包施工项异常" + e.getMessage();
            throw new PackageException(message);
        }
        return new ServiceResponse(true, "创建礼包施工项成功");
    }

    public PageRespBean<PackageConstructResponseBean> getPackageConstruct(Long packageVersionId,
                                                                          Long constructId,
                                                                          Integer offSet,
                                                                          Integer limit) throws PackageException {
        PageRespBean<PackageConstructResponseBean> responsePage = new PageRespBean<>();
        Criteria criatira = new Criteria();
        if (packageVersionId != null) criatira.and(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(packageVersionId);
        if (constructId != null) criatira.and(CommonStaticConst.Package.CONSTRUCT_ID).is(constructId);
        Query query = new Query(criatira);

        try {
            List<PackageConstruct> packageConstructs = mongoTemplate.find(query, PackageConstruct.class);
            List<PackageConstructResponseBean> responseList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(packageConstructs)) {
                responsePage.setTotal(packageConstructs.size());
                if (offSet != null && limit != null) query.skip((offSet - 1) * limit);
                if (limit != null) query.limit(limit);
                List<PackageConstruct> limitPkgConstructs = mongoTemplate.find(query, PackageConstruct.class);
                responseList = limitPkgConstructs.stream().map(con -> {
                    PackageConstructResponseBean responseBean = new PackageConstructResponseBean();
                    Construct c = cacheUtil.getCachedConstruct(String.valueOf(con.getConstructId()));
                    responseBean.setPackageConstructId(con.getPackageConstructId());
                    responseBean.setConstructId(con.getConstructId());
                    responseBean.setConstructName(c.getConstructName());
                    responseBean.setConstructCode(c.getConstructCode());
                    responseBean.setConstructionPrice(c.getCustomerPrice());
                    responseBean.setCustomerPrice(c.getCustomerPrice());
                    responseBean.setHouseType(con.getHouseType());
                    responseBean.setUnitCode(c.getUnit());
                    responseBean.setLimit(con.getLimitQuantity());
                    responseBean.setHasLimitation(con.getLimitQuantity().intValue() > 0);
                    responseBean.setPackageVersion(con.getPackageVersion());
                    return responseBean;
                }).collect(Collectors.toList());
            }
            responsePage.setData(responseList);
        } catch (Exception e) {
            logger.error("获取套餐施工项异常", e.getMessage());
            throw new PackageException(e.getMessage());
        }
        return responsePage;
    }


    /**
     * 更新施工项信息
     *
     * @param source
     * @return
     */
    public CommonRespBean updateConstruct(ConstructBean source) throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_ID, source.getConstructId());
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            Update update = new Update();
            update.set(CommonStaticConst.Construct.CONSTRUCT_NAME, source.getConstructName());
            update.set(CommonStaticConst.Construct.UNIT_CODE, source.getUnitCode());
            update.set(CommonStaticConst.Construct.DESC, source.getDesc());
            update.set(CommonStaticConst.Construct.ASSIT_SPEC, source.getAssitSpec());
            update.set(CommonStaticConst.Construct.STANDARD, source.getStandard());
            update.set(CommonStaticConst.Construct.SOURCE_OF_STANDARD, source.getSourceOfStandard());
            update.set(CommonStaticConst.Construct.REMARK, source.getRemark());
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, source.getUpdateBy());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, ConstructPo.class);
            if (writeResult.getN() > 0) {
                logger.info("更新施工项成功：{}", writeResult.getN());
                cacheUtil.initConstruct();
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新施工项成功"));
        } catch (Exception e) {
            logger.error("更新施工项异常{}", e.getMessage());
            throw new PackageException("更新施工项异常");
        }

    }

    /**
     * 校验施工项编码是否已经存在
     *
     * @param constructCode
     * @return
     */
    public CommonRespBean checkConstructCode(String decorationCompany, String constructCode) throws PackageException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("construct.constructCode"),
                            propertiesUtils.getProperty("common.status"),
                            propertiesUtils.getProperty("common.decorationCompany")
                    },
                    new Object[]{constructCode,
                            CommonStaticConst.Common.ENABLE_STATUS,
                            decorationCompany
                    });
            ConstructPo constructPo = findPoByUniqueKey(props, ConstructPo.class);
            if (constructPo != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "编码已存在"));
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "编码可用"));
        } catch (Exception e) {
            logger.error("校验施工项编码异常:{}", e.getMessage());
            throw new PackageException("校验施工项编码异常，请重试");
        }
    }

    /**
     * 列表查询施工项
     *
     * @param source 查询条件
     * @return
     * @throws PackageException
     */
    public PageRespBean getConstructList(QueryConstructBean source) throws PackageException {

        PageRespBean<ConstructBean> page = null;
        try {
            page = new PageRespBean<>();
            BasicDBObject basicDBObject = new BasicDBObject();

            if (StringUtils.isNotBlank(source.getConstructCategory())) {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CATEGORY, source.getConstructCategory());
            }
            if (StringUtils.isNotBlank(source.getConstructItem())) {
                Pattern pattern = Pattern.compile("^.*" + source.getConstructItem() + ".*$", Pattern.CASE_INSENSITIVE);
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_ITEM, new BasicDBObject("$regex", pattern));
            }
            if (StringUtils.isNotBlank(source.getConstructCode())) {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, source.getConstructCode());
            }
            if (StringUtils.isNotBlank(source.getConstructName())) {
                Pattern pattern = Pattern.compile("^.*" + source.getConstructName() + ".*$", Pattern.CASE_INSENSITIVE);
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_NAME, new BasicDBObject("$regex", pattern));
            }
            if (StringUtils.isNotBlank(source.getDecorationCompany())) {
                basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());
            }
            if (StringUtils.isNotBlank(source.getBatchNum())) {
                basicDBObject.put(propertiesUtils.getProperty("constructPrice.batchNumber"), source.getBatchNum());
            }
            if (source.getStatus() != null) {
                basicDBObject.put(propertiesUtils.getProperty("common.status"), source.getStatus());
            } else {
                basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            }

            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            long count = mongoTemplate.count(basicQuery, ConstructPo.class);
            basicQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
            page.setTotal(count);
            basicQuery.skip((source.getOffset() - 1) * source.getLimit());
            basicQuery.limit(source.getLimit());
            List<ConstructPo> constructPos = mongoTemplate.find(basicQuery, ConstructPo.class);
            List<ConstructBean> constructs = constructPos.stream().map(constructPo -> {
                ConstructBean constructBean = new ConstructBean();
                BeanUtils.copyProperties(constructPo, constructBean);
                return constructBean;
            }).collect(Collectors.toList());
            page.setOffset(source.getOffset());
            page.setLimit(source.getLimit());
            page.setData(constructs);
        } catch (Exception e) {
            logger.error("列表查询施工项:{}", e.getMessage());
            throw new PackageException("列表查询施工项异常");
        }
        return page;
    }

    /**
     * 批量创建施工项
     *
     * @param inputStream
     * @param user
     * @return
     * @throws PackageException
     */
    public CommonRespBean batchCreateConstruct(InputStream inputStream,
                                               FormDataContentDisposition file,
                                               String user,
                                               String batchNum,
                                               String decorationCompany) throws PackageException {
        List<BatchUploadConstructRespBean> responseList = new ArrayList<>();
        PageRespBean<BatchUploadConstructPo> responsePage = new PageRespBean<>();
        List<BatchUploadConstructPo> batchConstructPos = new ArrayList<>();
        try {
            Workbook wb = null;
            String extString = file.getFileName().substring(file.getFileName().lastIndexOf("."));
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(inputStream);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL, "无效模板"));
            }
            Sheet sheet = wb.getSheetAt(0);
            int rownum = sheet.getPhysicalNumberOfRows();
            Row head = sheet.getRow(0);
            if (!validTemplate(head)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL, "无效模板"));
            }
            int colnum = head.getPhysicalNumberOfCells();
            //从第二行开始读。跳过表头
            for (int i = 1; i < rownum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                BatchUploadConstructRespBean respBean = new BatchUploadConstructRespBean();
                ConstructBean constructBean = new ConstructBean();
                try {
                    String cellData;
                    String filed;
                    for (int j = 0; j < colnum; j++) {
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        filed = (String) getCellFormatValue(head.getCell(j));
                        initConstructBean(constructBean, filed, cellData);
                    }
                    constructBean.setDecorationCompany(decorationCompany);
                    constructBean.setBatchNum(batchNum);
                    constructBean.setUpdateBy(user);
                    validConstructBean(constructBean);
                    CommonRespBean insertResponse = insertConstruct(constructBean);
                    if(!insertResponse.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)){
                        throw new PackageException(insertResponse.getStatus().getMessage());
                    }
                    logger.info("批量导入，施工项{}创建成功", constructBean.toString());
                    ConstructPo constructPo = cacheUtil.getCachedIndiviConstruct(constructBean.getConstructCode());
                    BeanUtils.copyProperties(constructPo, respBean);
                    respBean.setResult(1);
                    responseList.add(respBean);
                } catch (PackageException e) {
                    logger.error("批量导入施工项中，施工项{}创建失败", constructBean.getConstructCode());
                    BeanUtils.copyProperties(constructBean, respBean);
                    respBean.setResult(0);
                    respBean.setMessage(e.getErrorMsg());
                    responseList.add(respBean);
                }
            }
            batchConstructPos = responseList.stream().map(batchRespBean -> {
                BatchUploadConstructPo batchConstructPo = new BatchUploadConstructPo();
                BeanUtils.copyProperties(batchRespBean, batchConstructPo);
                batchConstructPo.setBatchNum(batchNum);
                batchConstructPo.setBatchResultId(keyManagerUtil.get16PlacesId());
                batchConstructPo.setLogStatus(1);
                batchConstructPo.setUpdateTime(batchConstructPo.getCreateTime());
                return batchConstructPo;
            }).collect(Collectors.toList());
            mongoTemplate.insertAll(batchConstructPos);
            if (!CollectionUtils.isEmpty(batchConstructPos)) {
                responsePage.setTotal(batchConstructPos.size());
                responsePage.setLimit(CommonStaticConst.Common.DEFAULT_LIMIT);
                responsePage.setOffset(CommonStaticConst.Common.DEFAULT_OFFSET);
            }

        } catch (IOException e) {
            logger.error("批量创建施工项异常:{}", e.getMessage());
            throw new PackageException("批量创建施工项异常");
        }
        return new CommonRespBean(batchConstructPos,
                new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "批量创建施工项结束"),
                responsePage);
    }

    /**
     * 逻辑删除施工项
     *
     * @param idList
     * @return
     * @throws PackageException
     */
    @LogAnnotation
    public CommonRespBean deleteConstructPrices(List<Long> idList, String updateBy) throws PackageException {
        try {
            Criteria criteria = Criteria.where(propertiesUtils.getProperty("constructPrice.constructPriceId")).in(idList);
            Update update = new Update();
            update.set(CommonStaticConst.Construct.STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            update.set(CommonStaticConst.Common.UPDATE_BY, updateBy);
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            WriteResult writeResult = mongoTemplate.updateMulti(new Query(criteria), update, ConstructPricePo.class);
            cacheUtil.initIndividualConstruct();
            if (writeResult != null) {
                logger.info("共删除{}条记录", writeResult.getN());
            }
        } catch (BeansException e) {
            logger.error("删除施工项价格出现异常%s", e.getMessage());
            throw new PackageException("删除施工项价格出现异常");
        } catch (Exception e) {
            logger.error("删除施工项价格出现异常:%s", e);
            throw new PackageException("删除施工项价格出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除施工项价格成功"));
    }

    /**
     * 校验po的必填字段
     *
     * @param constructBean
     */
    private void validConstructBean(ConstructBean constructBean) throws PackageException {
        StringBuffer msg = new StringBuffer();
        if (StringUtils.isBlank(constructBean.getConstructCode())) {
            msg.append("施工项编码不能为空;");
        }
        if (StringUtils.isBlank(constructBean.getConstructItem())) {
            msg.append("施工项分类不能为空;");
        }
        if (StringUtils.isBlank(constructBean.getConstructName())) {
            msg.append("施工项名称不能为空;");
        }
        if (StringUtils.isBlank(constructBean.getUnitCode())) {
            msg.append("计量单位不能为空;");
        }
        if (StringUtils.isBlank(constructBean.getUpdateBy())) {
            msg.append("更新人不能为空;");
        }
        if (StringUtils.isBlank(constructBean.getDecorationCompany())) {
            msg.append("装饰公司不能为空;");
        }

        if (msg.length() > 0) {
            throw new PackageException(msg.toString());
        }
    }

    private boolean validTemplate(Row head) {
        int row = head.getPhysicalNumberOfCells();
        if (!CommonStaticConst.Common.BATCH_TEMPLATE_FILED_NUM.equals(row)) {
            return false;
        }
        String cellData;
        for (int i = 0; i < row; i++) {
            int finalI = i;
            cellData = (String) getCellFormatValue(head.getCell(i));
            Optional<BatchTemplateFileds> matchEnum =
                    Arrays.stream(BatchTemplateFileds.values())
                            .filter(en -> en.getIndex()
                                    .equals(finalI))
                            .findFirst();
            if (!matchEnum.isPresent()) {
                return false;
            }
            if (!matchEnum.get().getFiled().equalsIgnoreCase(cellData)) {
                return false;
            }
        }
        return true;
    }

    private void initConstructBean(ConstructBean constructBean, String filed, String value) {
        switch (filed) {
            case "施工项类别":
                constructBean.setConstructCategory(CommonStaticConst.Construct.DEFAULT_CONSTRUCT_CATEGORY);
                break;
            case "施工项分类":
                constructBean.setConstructItem(value);
                break;
            case "施工项编码":
                constructBean.setConstructCode(value);
                break;
            case "施工项名称":
                constructBean.setConstructName(value);
                break;
            case "计量单位":
                constructBean.setUnitCode(value);
                break;
            case "工艺材料简介":
                constructBean.setDesc(value);
                break;
            case "辅料名称规格":
                constructBean.setAssitSpec(value);
                break;
            case "验收标准":
                constructBean.setStandard(value);
                break;
            case "标准出处":
                constructBean.setSourceOfStandard(value);
                break;
            case "备注":
                constructBean.setRemark(value);
                break;
        }
    }

    private Object getCellFormatValue(Cell cell) {
        Object cellValue;
        if (cell != null) {
            if (cell.getCellType().name().equals(CellType.NUMERIC.name())) {
                cellValue = String.valueOf(new Double(cell.getNumericCellValue()).intValue());
            } else {
                cellValue = cell.getRichStringCellValue().getString();
            }

        } else {
            cellValue = "";
        }
        return cellValue;
    }

    public CommonRespBean getBatchUploadResult(String batchNum,
                                               Long uploadDate,
                                               String user,
                                               Integer uploadResult,
                                               String decorationCompany,
                                               Integer offSet,
                                               Integer limit) throws PackageException {
        Criteria criatira = new Criteria();
        if (!StringUtils.isEmpty(batchNum)) criatira.and(CommonStaticConst.Construct.BATCH_NUMBER).is(batchNum);
        if (uploadDate != null) {
            criatira.and(CommonStaticConst.Common.CREATE_TIME)
                    .lt(new Date(uploadDate + CommonStaticConst.Common.TIME_FOR_ONE_DAY))
                    .gte(new Date(uploadDate));
        }
        if (!StringUtils.isEmpty(user)) criatira.and(CommonStaticConst.Common.UPDATE_BY).is(user);
        if (!StringUtils.isEmpty(decorationCompany)) criatira.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        if (uploadResult != null) criatira.and(CommonStaticConst.Construct.RESULT).is(uploadResult.intValue());
        criatira.and(CommonStaticConst.Construct.LOG_STATUS).is(CommonStaticConst.Common.ENABLE_STATUS);
        PageRespBean<BatchUploadConstructPo> responsePage = new PageRespBean<>();
        Query query = new Query(criatira);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
        List<BatchUploadConstructPo> responseList = new ArrayList<>();
        try {
            long count = mongoTemplate.count(query, BatchUploadConstructPo.class);
            responsePage.setTotal(count);

            if (offSet != null && limit != null) query.skip((offSet - 1) * limit);
            if (limit != null) query.limit(limit);
            responseList = mongoTemplate.find(query, BatchUploadConstructPo.class);
        } catch (Exception e) {
            logger.error("查询批量录入施工项结果异常{}", e.getMessage());
            throw new PackageException("查询批量录入施工项结果异常");
        }
        responsePage.setLimit(limit.intValue());
        responsePage.setOffset(offSet.intValue());
        return new CommonRespBean(responseList, new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), responsePage);
    }

    public CommonRespBean removeBatchUploadResult(String batchResultIds) throws PackageException {

        try {
            List<String> resultIds = Arrays.asList(batchResultIds.split(","));
            List<Long> logIds = resultIds.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
            Criteria criteria = Criteria.where(CommonStaticConst.Construct.BATCH_RESULT_ID).in(logIds);
            Update update = new Update();
            update.set(CommonStaticConst.Construct.LOG_STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            WriteResult writeResult = mongoTemplate.updateMulti(new Query(criteria), update, BatchUploadConstructPo.class);
            if (writeResult != null) {
                logger.info("共删除{}条批处理记录", writeResult.getN());
            }
        } catch (Exception e) {
            logger.error("删除批处理记录异常{}", e.getMessage());
            throw new PackageException("删除批处理记录异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "操作成功"));
    }

    public CommonRespBean downloadConstructTemplate() throws PackageException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream ins = classloader.getResourceAsStream("BatchTemplate.xlsx");
        if (ins == null) {
            throw new PackageException("模板文件不存在");
        }
        StreamingOutput output = downloadTemplate(ins);
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "下载完成"), output);
    }

    public CommonRespBean verifyBatchNumber(String batchNumber) throws QuoteException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("constructPrice.batchNumber"), propertiesUtils.getProperty("common.status")},
                    new Object[]{batchNumber, CommonStaticConst.Common.ENABLE_STATUS});
            BatchUploadConstructPo batchPo = findPoByUniqueKey(props, BatchUploadConstructPo.class);
            if (batchPo != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "批处理号已存在"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "批处理号有效"));
            }
        } catch (RuntimeException e) {
            logger.error("验证批处理号异常", e.getMessage());
            throw new QuoteException("验证批处理号异常");
        }
    }

    public CommonRespBean updateConstructInBatch(BatchUploadConstructRequest source) throws PackageException {
        try {
            ConstructBean constructBean = new ConstructBean();
            BeanUtils.copyProperties(source, constructBean);
            validConstructBean(constructBean);
            CommonRespBean insertResponse = insertConstruct(constructBean);
            if(!insertResponse.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)){
                return insertResponse;
            }
            BatchUploadConstructPo constructPo = new BatchUploadConstructPo();
            BeanUtils.copyProperties(source, constructPo);
            constructPo.setLogStatus(CommonStaticConst.Common.ENABLE_STATUS);
            constructPo.setResult(CommonStaticConst.Common.ENABLE_STATUS);
            constructPo.setBatchResultId(keyManagerUtil.get16PlacesId());
            constructPo.setMessage("");
            constructPo.setCreateTime(new Date());
            mongoTemplate.insert(constructPo);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "操作成功"));
        } catch (BeansException e) {
            logger.error("更新批处理施工项异常", e.getMessage());
            throw new PackageException("更新批处理施工项异常");
        } catch (PackageException e) {
            logger.error("更新批处理施工项异常", e.getMessage());
            throw e;
        }
    }

    /**
     * 施工项导出
     *
     * @return
     */
    public CommonRespBean<OutputStream> exportConstruct(QueryExportConstructbean query) throws PackageException {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (StringUtils.isNotBlank(query.getConstructCategory())) {
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CATEGORY, query.getConstructCategory());
        }
        if (StringUtils.isNotBlank(query.getConstructCode())) {
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, query.getConstructCode());
        }
        if (StringUtils.isNotBlank(query.getConstructItem())) {
            Pattern pattern = Pattern.compile("^.*" + query.getConstructItem() + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_ITEM, new BasicDBObject("$regex", pattern));
        }
        if (StringUtils.isNotBlank(query.getConstructName())) {
            Pattern pattern = Pattern.compile("^.*" + query.getConstructName() + ".*$", Pattern.CASE_INSENSITIVE);
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_NAME, new BasicDBObject("$regex", pattern));
        }
        if (StringUtils.isNotBlank(query.getDecorationCompany())) {
            basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), query.getDecorationCompany());
        }
        if (query.getStatus() != null) {
            basicDBObject.put(CommonStaticConst.Construct.STATUS, query.getStatus());
        }
        BasicQuery basicQuery = new BasicQuery(basicDBObject);
        List<ConstructPo> allConstructs = mongoTemplate.find(basicQuery, ConstructPo.class);
        XSSFWorkbook wb = new XSSFWorkbook();
        //生成一个表格
        XSSFSheet sheet = wb.createSheet("系统施工项");
        XSSFRow headerRow = sheet.createRow(0);
        String[] headers = {"施工项类别", "施工项分类", "施工项编码", "施工项名称", "装饰公司", "计量单位", "工艺材料简介", "验收标准", "标准出处", "备注", "状态", "创建日期", "更新日期", "更新用户"};
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.setColumnWidth(i, 4000);
        }

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < allConstructs.size(); i++) {
            XSSFRow dateRow = sheet.createRow(i + 1);
            ConstructPo constructPo = allConstructs.get(i);
            dateRow.createCell(0).setCellValue(constructPo.getConstructCategory());
            dateRow.createCell(1).setCellValue(constructPo.getConstructItem());
            dateRow.createCell(2).setCellValue(constructPo.getConstructCode());
            dateRow.createCell(3).setCellValue(constructPo.getConstructName());
            dateRow.createCell(4).setCellValue(constructPo.getDecorationCompany());
            dateRow.createCell(5).setCellValue(constructPo.getUnitCode());
            dateRow.createCell(6).setCellValue(constructPo.getDesc());
            dateRow.createCell(7).setCellValue(constructPo.getStandard());
            dateRow.createCell(8).setCellValue(constructPo.getSourceOfStandard());
            dateRow.createCell(9).setCellValue(constructPo.getRemark());
            dateRow.createCell(10).setCellValue(getStatus(constructPo.getStatus()));
            StatusEnum[] values = StatusEnum.values();
            if (constructPo.getCreateTime() != null) {
                dateRow.createCell(11).setCellValue(spf.format(constructPo.getCreateTime()));
            }
            if (constructPo.getUpdateTime() != null) {
                dateRow.createCell(12).setCellValue(spf.format(constructPo.getUpdateTime()));
            }
            dateRow.createCell(13).setCellValue(constructPo.getUpdateBy());
            if (i == allConstructs.size() - 1) {
                logger.info("================>excel组装完毕");
            }
        }
        try {
            StreamingOutput out = output -> {
                wb.write(output);
            };
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "下载完成"), out);
        } catch (Exception e) {
            logger.error("施工项导出异常{}", e.getMessage());
            throw new PackageException("施工项导出异常");
        }

    }



    /**
     * 获取状态名称
     *
     * @param status
     * @return
     */
    private String getStatus(Integer status) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum e : values) {
            if (e.getCode().equals(status)) {
                return e.getValue();
            }
        }
        return StatusEnum.OK.getValue();
    }

    /**
     * 条件查询施工项价格列表
     *
     * @param source 条件列表
     * @return .
     * @throws PackageException .
     */
    public PageRespBean getConstructPriceList(QueryConstructPriceBean source) throws ParentException {
        PageRespBean pageRespBean = new PageRespBean<>();

        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (StringUtils.isNotBlank(source.getConstructCode())) {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, source.getConstructCode());
            }
            if (StringUtils.isNotBlank(source.getDecorationCompany())) {
                basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());
            }
            if (source.getDecorationTypeId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("constructPrice.decorationTypeId"), source.getDecorationTypeId());
            }
            if (source.getQuoteVersionId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), source.getQuoteVersionId());
            }
            if (source.getQuoteTypeId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), source.getQuoteTypeId());
            }
            if (StringUtils.isNotBlank(source.getBatchNum())) {
                basicDBObject.put(propertiesUtils.getProperty("constructPrice.batchNumber"), source.getBatchNum());
            }
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            basicQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
            long count = mongoTemplate.count(basicQuery, ConstructPricePo.class);
            pageRespBean.setTotal(count);
            basicQuery.skip((source.getOffset() - 1) * source.getLimit());
            basicQuery.limit(source.getLimit());
            List<ConstructPricePo> constructPricePos = mongoTemplate.find(basicQuery, ConstructPricePo.class);
            List<ConstructPriceBean> resList = Lists.newArrayList();
            for (ConstructPricePo price:constructPricePos) {
                ConstructPriceBean constructPriceBean = new ConstructPriceBean();
                BeanUtils.copyProperties(price, constructPriceBean);
                Properties primaryKey = new Properties();
                if (price.getDecorationTypeId() != null) {
                    primaryKey.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), price.getDecorationTypeId());
                    primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                    DecorationTypePo decorationTypePo = findPoByUniqueKey(primaryKey, DecorationTypePo.class);
                    if (decorationTypePo != null) constructPriceBean.setDecorationType(decorationTypePo.getName());
                }
                if (price.getQuoteTypeId() != null) {
                    primaryKey.clear();
                    primaryKey.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), price.getQuoteTypeId());
                    primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                    QuoteTypePo quoteTypePo = findPoByUniqueKey(primaryKey, QuoteTypePo.class);
                    if (quoteTypePo != null) constructPriceBean.setQuoteType(quoteTypePo.getName());
                }
                if (price.getQuoteVersionId() != null) {
                    primaryKey.clear();
                    primaryKey.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), price.getQuoteVersionId());
                    primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                    QuoteVersionPo quoteVersionPo = findPoByUniqueKey(primaryKey, QuoteVersionPo.class);
                    if (quoteVersionPo != null) constructPriceBean.setQuoteVersion(quoteVersionPo.getQuoteVersionCode());
                }
                if (price.getHouseTypeId() != null) {
                    primaryKey.clear();
                    primaryKey.put(propertiesUtils.getProperty("houseType.houseTypeId"), price.getHouseTypeId());
                    primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                    HouseTypePo houseTypePo = findPoByUniqueKey(primaryKey, HouseTypePo.class);
                    if (houseTypePo != null) constructPriceBean.setHouseType(houseTypePo.getHouseType());
                }
                resList.add(constructPriceBean);
            }
            pageRespBean.setOffset(source.getOffset());
            pageRespBean.setLimit(source.getLimit());
            pageRespBean.setData(resList);
            return pageRespBean;
        } catch (Exception e) {
            logger.error("列表查询施工项价格异常:{}", e.getMessage());
            throw new PackageException("列表查询施工项价格异常");
        }
    }

    /**
     * 新增施工项价格实体
     *
     * @param source 施工项价格参数
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean insertConstructPrice(ConstructPriceBean source) throws PackageException {
        CommonRespBean checkResult = checkConstructPrice(source);
        if (checkResult.getStatus().getCode().equals(CommonRespBean.Status.DATA_EXISTING)) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "施工项价格存在！"));
        }
        if (!checkResult.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)) {
            return checkResult;
        }
        try {
            ConstructPricePo constructPricePo = new ConstructPricePo();
            validConstructPriceBean(source);
            BeanUtils.copyProperties(source, constructPricePo);
            constructPricePo.setConstructPriceId(source.getConstructPriceId() != null ? source.getConstructPriceId() : keyManagerUtil.getUniqueId());
            constructPricePo.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            constructPricePo.setCreateTime(new Date());
            constructPricePo.setUpdateTime(constructPricePo.getCreateTime());
            mongoTemplate.insert(constructPricePo);
            logger.info("创建施工项价格成功：{}", constructPricePo.toString());
        } catch (BeansException e) {
            logger.error("创建施工项价格出现异常{}", e.getMessage());
            throw new PackageException("创建施工项价格出现异常");
        } catch (RuntimeException e) {
            logger.error("创建施工项价格出现异常{}", e.getMessage());
            throw new PackageException("创建施工项价格出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "添加施工项成功"));
    }

    /**
     * 校验施工项价格是否可创建
     *
     * @param priceBean
     * @return
     */
    public CommonRespBean checkConstructPrice(ConstructPriceBean priceBean) throws PackageException {
        try {
            CommonRespBean commonRespBean = checkDataExist(priceBean);
            if (!commonRespBean.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)) {
                return commonRespBean;
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, priceBean.getConstructCode());
            basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), priceBean.getQuoteVersionId());
            basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), priceBean.getQuoteTypeId());
            basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), priceBean.getDecorationTypeId());
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            boolean exists = mongoTemplate.exists(new BasicQuery(basicDBObject), ConstructPricePo.class);
            if (exists) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "施工项价格已存在不可创建"));
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "施工项价格可创建"));
        } catch (Exception e) {
            logger.error("校验施工项价格异常:{}", e.getMessage());
            throw new PackageException("校验施工项价格异常");
        }
    }

    /**
     * 校验数据是否已在对应表里
     *
     * @param priceBean
     * @return .
     * @throws PackageException .
     */
    private CommonRespBean checkDataExist(ConstructPriceBean priceBean) throws QuoteException {
        Properties props = null;
        try {
            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("decorationType.decorationTypeId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{priceBean.getDecorationTypeId(), CommonStaticConst.Common.ENABLE_STATUS});

            DecorationTypePo decorationTypePo = quoteTypeService.findPoByUniqueKey(props, DecorationTypePo.class);
            if (decorationTypePo == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "装修类型不存在"));
            }

            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("quoteVersion.quoteVersionId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{priceBean.getQuoteVersionId(), CommonStaticConst.Common.ENABLE_STATUS});
            QuoteVersionPo quoteVersion = quoteVersionService.findPoByUniqueKey(props, QuoteVersionPo.class);
            if (quoteVersion == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "报价版本不存在"));
            }
            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("quoteType.quoteTypeId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{priceBean.getQuoteTypeId(), CommonStaticConst.Common.ENABLE_STATUS});

            QuoteTypePo quoteType = quoteTypeService.findPoByUniqueKey(props, QuoteTypePo.class);
            if (quoteType == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "报价类型不存在"));
            }
            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("construct.constructCode"), propertiesUtils.getProperty("common.status")},
                    new Object[]{priceBean.getConstructCode(), CommonStaticConst.Common.ENABLE_STATUS});
            ConstructPo constructPo = findPoByUniqueKey(props, ConstructPo.class);
            if (constructPo == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "施工项编码不存在"));
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "数据验证通过！"));
        } catch (Exception e) {
            logger.error("校验报价版本，报价类型，施工项编码异常:{}", e.getMessage());
            throw new QuoteException("校验报价版本，报价类型，施工项编码异常");
        }
    }


    /**
     * 更新施工项价格po
     *
     * @param source put参数
     * @return .
     * @throws PackageException .
     */
    private int updateConstructFromConstructPriceBean(ConstructPriceBean source) throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (source.getConstructPriceId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("constructPrice.constructPriceId"), source.getConstructPriceId());
            } else {
                basicDBObject.put(CommonStaticConst.Construct.CONSTRUCT_CODE, source.getConstructCode());
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), source.getQuoteVersionId());
                basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), source.getQuoteTypeId());
                basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), source.getDecorationTypeId());
            }
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), source.getQuoteVersionId());
            update.set(propertiesUtils.getProperty("quoteType.quoteTypeId"), source.getQuoteTypeId());
            update.set(propertiesUtils.getProperty("constructPrice.customerPrice"), source.getCustomerPrice());
            update.set(propertiesUtils.getProperty("constructPrice.foremanPrice"), source.getForemanPrice());
            update.set(propertiesUtils.getProperty("constructPrice.houseTypeId"), source.getHouseTypeId());
            update.set(propertiesUtils.getProperty("constructPrice.limit"), source.getLimit());
            update.set(propertiesUtils.getProperty("constructPrice.limitAmount"), source.getLimitAmount());
            update.set(CommonStaticConst.Construct.UNIT_CODE, source.getUnitCode());
            update.set(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(propertiesUtils.getProperty("common.updateBy"), source.getUpdateBy());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, ConstructPricePo.class);
            if (writeResult.getN() > 0) {
                logger.info("更新施工项价格成功：{}", source.toString());
            } else {
                logger.info("更新施工项价格失败：{}", source.toString());
            }
            return writeResult.getN();
        } catch (Exception e) {
            logger.error("更新施工项价格异常{}", e.getMessage());
            throw new PackageException("更新施工项价格异常");
        }
    }

    /**
     * 更新施工项价格
     *
     * @param source 施工项价格参数
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean updateConstructPrice(ConstructPriceBean source) throws PackageException {
        try {
            int result = updateConstructFromConstructPriceBean(source);
            if (result > 0) {
                logger.info("更新施工项价格成功：{}", result);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新施工项价格成功"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL, "更新施工项价格失败"));
            }
        } catch (Exception e) {
            logger.error("更新施工项价格异常{}", e.getMessage());
            throw new PackageException("更新施工项价格异常");
        }

    }

    /**
     * 批量创建施工项价格
     *
     * @param inputStream
     * @param user
     * @return
     * @throws ParentException
     */
    public CommonRespBean batchCreateConstructPrice(InputStream inputStream,
                                                    FormDataContentDisposition file,
                                                    String user,
                                                    String batchNum,
                                                    String decorationCompany) throws ParentException {
        Properties props = propertiesUtils.buildProperties(
                new String[]{propertiesUtils.getProperty("constructPrice.batchNumber"), propertiesUtils.getProperty("common.status")},
                new Object[]{batchNum, CommonStaticConst.Common.ENABLE_STATUS});
        BatchUploadConstructPricePo batchPo = findPoByUniqueKey(props, BatchUploadConstructPricePo.class);
        if (batchPo != null) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "批处理号已存在"));
        }
        List<BatchUploadConstructPriceRespBean> responseList = new ArrayList<>();
        PageRespBean<BatchUploadConstructPo> responsePage = new PageRespBean<>();
        List<BatchUploadConstructPricePo> batchConstructPricePos = new ArrayList<>();
        try {
            Workbook wb;
            String extString = file.getFileName().substring(file.getFileName().lastIndexOf("."));
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(inputStream);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL, "无效模板"));
            }
            Sheet sheet = wb.getSheetAt(0);
            int rowNum = sheet.getPhysicalNumberOfRows();
            Row head = sheet.getRow(0);
            if (!validConstructPriceTemplate(head)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL, "无效模板"));
            }
            int column = head.getPhysicalNumberOfCells();
            //从第二行开始读。跳过表头
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                BatchUploadConstructPriceRespBean respBean = new BatchUploadConstructPriceRespBean();
                ConstructPriceBean constructPriceBean = new ConstructPriceBean();
                try {
                    String cellData;
                    String filed;
                    constructPriceBean.setDecorationCompany(decorationCompany);
                    constructPriceBean.setBatchNum(batchNum);
                    StringBuilder errMsg = new StringBuilder();
                    for (int j = 0; j < column; j++) {
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        cellData = StringUtils.isEmpty(cellData)? "":cellData;
                        filed = (String) getCellFormatValue(head.getCell(j));
                        try {
                            initConstructPriceBean(constructPriceBean, filed, cellData);
                        } catch (QuoteException e) {
                            errMsg.append(e.getErrorMsg());
                            errMsg.append(";");
                        }
                    }
                    if(StringUtils.isNotBlank(errMsg.toString())) {
                        throw new QuoteException(errMsg.toString());
                    }
                    constructPriceBean.setUpdateBy(user);
                    validConstructPriceBean(constructPriceBean);
                    CommonRespBean commonRespBean = insertConstructPrice(constructPriceBean);
                    if (!commonRespBean.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)) {
                        throw new QuoteException(commonRespBean.getStatus().getMessage());
                    }
                    logger.info("批量导入，施工项{}创建成功", constructPriceBean.toString());
                    BeanUtils.copyProperties(constructPriceBean, respBean);
                    respBean.setResult(1);
                    responseList.add(respBean);
                } catch (ParentException e) {
                    logger.error("批量导入施工项中，施工项价格{}创建失败", JSON.toJSONString(constructPriceBean));
                    BeanUtils.copyProperties(constructPriceBean, respBean);
                    respBean.setResult(0);
                    respBean.setMessage(e.getErrorMsg());
                    responseList.add(respBean);
                }
            }
            batchConstructPricePos = responseList.stream().map(batchRespBean -> {
                BatchUploadConstructPricePo batchUploadConstructPricePo = new BatchUploadConstructPricePo();
                BeanUtils.copyProperties(batchRespBean, batchUploadConstructPricePo);
                batchUploadConstructPricePo.setUpdateBy(user);
                batchUploadConstructPricePo.setCreateTime(new Date());
                batchUploadConstructPricePo.setUpdateTime(batchUploadConstructPricePo.getCreateTime());
                batchUploadConstructPricePo.setBatchNum(batchNum);
                batchUploadConstructPricePo.setBatchResultId(keyManagerUtil.get16PlacesId());
                batchUploadConstructPricePo.setLogStatus(1);
                return batchUploadConstructPricePo;
            }).collect(Collectors.toList());
            mongoTemplate.insertAll(batchConstructPricePos);
            if (!CollectionUtils.isEmpty(batchConstructPricePos)) {
                responsePage.setTotal(batchConstructPricePos.size());
                responsePage.setLimit(CommonStaticConst.Common.DEFAULT_LIMIT);
                responsePage.setOffset(CommonStaticConst.Common.DEFAULT_OFFSET);
            }

        } catch (IOException e) {
            logger.error("批量创建施工项异常:{}", e.getMessage());
            throw new PackageException("批量创建施工项异常");
        }
        return new CommonRespBean(batchConstructPricePos, new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "批量创建施工项结束"), responsePage);
    }


    /**
     * 验证施工项价格模版excel
     *
     * @param head .
     * @return .
     */
    private boolean validConstructPriceTemplate(Row head) {
        int row = head.getPhysicalNumberOfCells();
        if (!Integer.valueOf(propertiesUtils.getProperty("constructPrice.template.valid.rowNum")).equals(row)) {
            return false;
        }
        String cellData;
        for (int i = 0; i < row; i++) {
            int finalI = i;
            cellData = (String) getCellFormatValue(head.getCell(i));
            Optional<ConstructPriceTemplateEnum> matchEnum =
                    Arrays.stream(ConstructPriceTemplateEnum.values()).filter(en -> en.getIndex().equals(finalI)).findFirst();
            if (!matchEnum.isPresent()) {
                return false;
            }
            if (!matchEnum.get().getFiled().equalsIgnoreCase(cellData)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化施工项价格模版
     *
     * @param constructPriceBean 实体bean
     * @param filed              字段
     * @param value              字段的值
     */
    private void initConstructPriceBean(ConstructPriceBean constructPriceBean, String filed, String value) throws QuoteException {
        Properties props;
        switch (filed) {
            case "装修类型":
                if(StringUtils.isEmpty(value)){
                    throw new QuoteException(String.format("未提供装修类型"));
                }
                props = propertiesUtils.buildProperties(
                        new String[]{propertiesUtils.getProperty("common.name"),
                                propertiesUtils.getProperty("common.status"),
                                propertiesUtils.getProperty("common.decorationCompany")
                        },
                        new Object[]{value,
                                CommonStaticConst.Common.ENABLE_STATUS,
                                constructPriceBean.getDecorationCompany()
                        });
                DecorationTypePo decorationType = decorationTypeService.findPoByUniqueKey(props, DecorationTypePo.class);
                constructPriceBean.setDecorationType(value);
                if (decorationType != null) {
                    constructPriceBean.setDecorationTypeId(decorationType.getDecorationTypeId());
                } else {
                    throw new QuoteException(String.format("装修类型[%s]不存在", value));
                }
                break;
            case "报价版本":
                if(StringUtils.isEmpty(value)){
                    throw new QuoteException(String.format("未提供报价版本"));
                }
                props = propertiesUtils.buildProperties(
                        new String[]{propertiesUtils.getProperty("quoteVersion.quoteVersionCode"),
                                propertiesUtils.getProperty("common.status"),
                                propertiesUtils.getProperty("common.decorationCompany")
                        },
                        new Object[]{value,
                                CommonStaticConst.Common.ENABLE_STATUS,
                                constructPriceBean.getDecorationCompany()
                        });
                QuoteVersionPo quoteVersion = quoteVersionService.findPoByUniqueKey(props,
                        QuoteVersionPo.class);
                constructPriceBean.setQuoteVersion(value);
                if (quoteVersion != null) {
                    constructPriceBean.setQuoteVersionId(quoteVersion.getQuoteVersionId());
                } else {
                    throw new QuoteException(String.format("报价版本[%s]不存在", value));
                }
                break;
            case "报价类型":
                if(StringUtils.isEmpty(value)){
                    throw new QuoteException(String.format("未提供报价类型"));
                }
                props = propertiesUtils.buildProperties(
                        new String[]{propertiesUtils.getProperty("common.name"),
                                propertiesUtils.getProperty("common.status"),
                                propertiesUtils.getProperty("decorationType.decorationTypeId"),
                                propertiesUtils.getProperty("common.decorationCompany")},
                        new Object[]{value,
                                CommonStaticConst.Common.ENABLE_STATUS,
                                constructPriceBean.getDecorationTypeId(),
                                constructPriceBean.getDecorationCompany()});
                QuoteTypePo quoteType = quoteTypeService.findPoByUniqueKey(props,
                        QuoteTypePo.class);
                constructPriceBean.setQuoteType(value);
                if (quoteType != null) {
                    constructPriceBean.setQuoteTypeId(quoteType.getQuoteTypeId());
                } else{
                    throw new QuoteException(String.format("报价类型[%s]不存在", value));
                }
                break;
            case "施工项编码":
                if(StringUtils.isEmpty(value)){
                    throw new QuoteException(String.format("未提供施工项编码"));
                }
                constructPriceBean.setConstructCode(value);
                ConstructPo constructPo = cacheUtil.getCachedIndiviConstruct(value);
                if (constructPo != null) {
                    constructPriceBean.setUnitCode(constructPo.getUnitCode());
                } else {
                    throw new QuoteException(String.format("施工项[%s]不存在", value));
                }
                break;
            case "客户报价":
                try {
                    if (StringUtils.isNotBlank(value)) {
                        constructPriceBean.setCustomerPrice(new BigDecimal(value));
                    }
                } catch (NumberFormatException ex) {
                    logger.info("客户报价格式异常：，{}", ex.toString());
                    throw new QuoteException("客户报价格式异常");
                }
                break;
            case "施工队报价":
                try {
                    if (StringUtils.isNotBlank(value)) {
                        constructPriceBean.setForemanPrice(new BigDecimal(value));
                    }
                } catch (NumberFormatException ex) {
                    logger.info("施工队报价格式异常：，{}", ex.toString());
                    throw new QuoteException("施工队报价格式异常");
                }
                break;
            case "户型":
                if(StringUtils.isEmpty(value)){
                    constructPriceBean.setHouseTypeId(0L);
                    constructPriceBean.setHouseType(value);
                    break;
                }
                props = propertiesUtils.buildProperties(
                        new String[]{propertiesUtils.getProperty("houseType.houseType"),
                                propertiesUtils.getProperty("common.status"),
                                propertiesUtils.getProperty("common.decorationCompany")
                        },
                        new Object[]{value,
                                CommonStaticConst.Common.ENABLE_STATUS,
                                constructPriceBean.getDecorationCompany()
                        });
                HouseTypePo houseType = packageRoomService.findPoByUniqueKey(props,
                        HouseTypePo.class);
                constructPriceBean.setHouseType(value);
                if (houseType != null) {
                    constructPriceBean.setHouseTypeId(houseType.getHouseTypeId());
                } else {
                    throw new QuoteException(String.format("户型[%s]不存在", value));
                }
                break;
            case "是否限量":
                constructPriceBean.setLimit(value.equals("是") ? true : false);
                break;
            case "限量值":
                if(constructPriceBean.getLimit() && StringUtils.isEmpty(value)){
                    throw new QuoteException("没有提供限量值");
                }
                try {
                    if (StringUtils.isNotBlank(value)) {
                        constructPriceBean.setLimitAmount(Integer.parseInt(value));
                    }
                } catch (NumberFormatException ex) {
                    logger.info("限量值格式化Integer 失败，{}", ex.toString());
                    throw new QuoteException("限量值格式错误，请只输入数字!");
                }
                break;
            default: break;
        }
    }

    /**
     * 校验po的必填字段
     *
     * @param constructPriceBean .
     * @throws PackageException .
     */
    private void validConstructPriceBean(ConstructPriceBean constructPriceBean) throws PackageException {
        StringBuffer msg = new StringBuffer();
        if (constructPriceBean.getDecorationTypeId() == null) {
            msg.append("装修类型为空或不存在;");
        }
        if (constructPriceBean.getQuoteVersionId() == null) {
            msg.append("报价版本为空或不存在;");
        }
        if (constructPriceBean.getQuoteTypeId() == null) {
            msg.append("报价类型为空或不存在;");
        }
        if (constructPriceBean.getCustomerPrice() == null) {
            msg.append("客户报价为空;");
        }
        if (constructPriceBean.getLimit() == null) {
            msg.append("是否限量为空;");
        }
        if (constructPriceBean.getLimit() && constructPriceBean.getLimitAmount() == null) {
            msg.append("限量值为空;");
        }
        if (msg.length() > 0) {
            throw new PackageException(msg.toString());
        }
    }


    /**
     * 条件查询批量上传施工项价格结果
     *
     * @param batchNum     批次号
     * @param uploadDate   上传日期
     * @param user         上传用户
     * @param uploadResult 上传结果
     * @param offSet       起始记录数
     * @param limit        页面大小
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean getBatchUploadConstructPriceResult(String batchNum,
                                                             Long uploadDate,
                                                             String user,
                                                             Integer uploadResult,
                                                             Integer offSet,
                                                             Integer limit,
                                                             String decorationCompany) throws PackageException {
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(batchNum)) criteria.and(CommonStaticConst.Construct.BATCH_NUMBER).is(batchNum);
        if (uploadDate != null) {
            criteria.and(CommonStaticConst.Common.CREATE_TIME)
                    .lt(new Date(uploadDate + CommonStaticConst.Common.TIME_FOR_ONE_DAY))
                    .gte(new Date(uploadDate));
        }
        if (!StringUtils.isEmpty(user)) criteria.and(propertiesUtils.getProperty("common.updateBy")).is(user);
        if (!StringUtils.isEmpty(decorationCompany)) criteria.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        if (uploadResult != null) criteria.and(CommonStaticConst.Construct.RESULT).is(uploadResult.intValue());
        criteria.and(CommonStaticConst.Construct.LOG_STATUS).is(CommonStaticConst.Common.ENABLE_STATUS);
        PageRespBean<BatchUploadConstructPriceRespBean> responsePage = new PageRespBean<>();
        Query query = new Query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
        List<BatchUploadConstructPriceRespBean> responseData = Lists.newArrayList();
        try {
            long count = mongoTemplate.count(query, BatchUploadConstructPricePo.class);
            responsePage.setTotal(count);

            if (offSet != null && limit != null) query.skip((offSet - 1) * limit);
            if (limit != null) query.limit(limit);
            List<BatchUploadConstructPricePo> priceList = mongoTemplate.find(query, BatchUploadConstructPricePo.class);
            if(!CollectionUtils.isEmpty(priceList)){
                try {
                    for(BatchUploadConstructPricePo price: priceList){
                        BatchUploadConstructPriceRespBean respBean = new BatchUploadConstructPriceRespBean();
                        BeanUtils.copyProperties(price, respBean);
                        Properties primaryKey = new Properties();
                        if(price.getDecorationTypeId() != null) {
                            primaryKey.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), price.getDecorationTypeId());
                            primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                            DecorationTypePo decorationTypePo = findPoByUniqueKey(primaryKey, DecorationTypePo.class);
                            if(decorationTypePo != null) respBean.setDecorationType(decorationTypePo.getName());
                        }
                        if(price.getQuoteTypeId() != null) {
                            primaryKey.clear();
                            primaryKey.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), price.getQuoteTypeId());
                            primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                            QuoteTypePo quoteTypePo = findPoByUniqueKey(primaryKey, QuoteTypePo.class);
                            if(quoteTypePo != null) respBean.setQuoteType(quoteTypePo.getName());
                        }
                        if(price.getQuoteVersionId() != null){
                            primaryKey.clear();
                            primaryKey.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), price.getQuoteVersionId());
                            primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                            QuoteVersionPo quoteVersionPo = findPoByUniqueKey(primaryKey, QuoteVersionPo.class);
                            if(quoteVersionPo != null) respBean.setQuoteVersion(quoteVersionPo.getQuoteVersionCode());
                        }
                        if(price.getHouseTypeId() != null) {
                            primaryKey.clear();
                            primaryKey.put(propertiesUtils.getProperty("houseType.houseTypeId"), price.getHouseTypeId());
                            primaryKey.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                            HouseTypePo houseTypePo = findPoByUniqueKey(primaryKey, HouseTypePo.class);
                            if(houseTypePo != null) respBean.setHouseType(houseTypePo.getHouseType());
                        }
                        responseData.add(respBean);
                    }
                } catch (QuoteException e){
                    logger.error("[PackageConstructService:getBatchUploadConstructPriceResult]获取施工项价格异常{}，", e.getErrorMsg());
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error("查询批量录入施工项结果异常{}", e.getMessage());
            throw new PackageException("查询批量录入施工项结果异常");
        }
        responsePage.setLimit(limit.intValue());
        responsePage.setOffset(offSet.intValue());
        return new CommonRespBean(responseData, new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), responsePage);
    }

    /**
     * 批量删除施工项价格上传结果
     *
     * @param batchResultIds id集合
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean removeBatchUploadConstructPriceResult(String batchResultIds) throws PackageException {
        try {
            List<String> resultIds = Arrays.asList(batchResultIds.split(","));
            List<Long> logIds = resultIds.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
            Criteria criteria = Criteria.where(CommonStaticConst.Construct.BATCH_RESULT_ID).in(logIds);
            Update update = new Update();
            update.set(CommonStaticConst.Construct.LOG_STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            WriteResult writeResult = mongoTemplate.updateMulti(new Query(criteria), update, BatchUploadConstructPricePo.class);
            if (writeResult != null) {
                logger.info("共删除{}条批处理记录", writeResult.getN());
            }
        } catch (Exception e) {
            logger.error("删除批处理记录异常{}", e.getMessage());
            throw new PackageException("删除批处理记录异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "操作成功"));
    }

    /**
     * 下载施工项价格模版
     *
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean downloadConstructPriceTemplate() throws PackageException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream ins = classloader.getResourceAsStream("ConstructPriceTemplate.xlsx");
        if (ins == null) {
            throw new PackageException("模板文件不存在");
        }
        StreamingOutput output = downloadTemplate(ins);
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "下载完成"), output);
    }


    /**
     * 下载模版
     *
     * @param finalIns .
     */
    private StreamingOutput downloadTemplate(InputStream finalIns) {
        StreamingOutput output = out -> {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = finalIns.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            finalIns.close();
        };
        return output;
    }


    /**
     * 验证批次号是否存在
     *
     * @param batchNum 批次号
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean checkConstructPriceBatchNum(String batchNum) throws QuoteException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("constructPrice.batchNumber")},
                    new Object[]{batchNum});
            BatchUploadConstructPricePo batchPo = findPoByUniqueKey(props,
                    BatchUploadConstructPricePo.class);
            if (batchPo != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "批处理号已存在"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "批处理号有效"));
            }
        } catch (RuntimeException e) {
            logger.error("验证批处理号异常", e.getMessage());
            throw new QuoteException("验证批处理号异常");
        }
    }

    /**
     * 编辑批处理中的施工项价格重新存储
     *
     * @param source request
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean updateConstructPriceInBatch(BatchUploadConstructPriceRequest source) throws PackageException {
        try {
            ConstructPriceBean constructPriceBean = new ConstructPriceBean();
            BeanUtils.copyProperties(source, constructPriceBean);
            validConstructPriceBean(constructPriceBean);
            CommonRespBean commonRespBean = insertConstructPrice(constructPriceBean);
            if (!commonRespBean.getStatus().getCode().equals(CommonRespBean.Status.SUCCESS)) {
                return commonRespBean;
            }
            BatchUploadConstructPricePo batchUploadConstructPricePo = new BatchUploadConstructPricePo();
            BeanUtils.copyProperties(source, batchUploadConstructPricePo);
            batchUploadConstructPricePo.setLogStatus(CommonStaticConst.Common.ENABLE_STATUS);
            batchUploadConstructPricePo.setResult(CommonStaticConst.Common.ENABLE_STATUS);
            batchUploadConstructPricePo.setBatchResultId(keyManagerUtil.get16PlacesId());
            batchUploadConstructPricePo.setMessage("");
            batchUploadConstructPricePo.setCreateTime(new Date());
            batchUploadConstructPricePo.setUpdateTime(new Date());
            mongoTemplate.insert(batchUploadConstructPricePo);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "操作成功"));
        } catch (BeansException e) {
            logger.error("更新批处理施工项异常", e.getMessage());
            throw new PackageException("更新批处理施工项异常");
        } catch (PackageException e) {
            logger.error("更新批处理施工项异常", e.getMessage());
            throw e;
        }
    }


    /**
     * 添加施工项计算公式
     *
     * @param requestBean 请求参数
     * @return .
     * @throws PackageException .
     */
    public CommonRespBean createConstructCalculationFormula(CalculationFormulaBean requestBean) throws QuoteException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("construct.constructId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{requestBean.getConstructId(), CommonStaticConst.Common.ENABLE_STATUS});
            ConstructPo constructPo = findPoByUniqueKey(props, ConstructPo.class);
            if (constructPo == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "施工项不存在"));
            }
            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("construct.constructId")}, new Object[]{requestBean.getConstructId()});
            CalculatorExpression expression = findPoByUniqueKey(props, CalculatorExpression.class);
            if (expression != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "施工项对应数量计算公式已存在"));
            }

            if (!verifyFormula(requestBean.getExpression())) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.INPUT_ERROR, "公式校验不通过"));
            }
            CalculatorExpression calculatorExpression = new CalculatorExpression();
            calculatorExpression.setExpressionId(keyManagerUtil.getUniqueId());
            calculatorExpression.setConstructId(requestBean.getConstructId());
            calculatorExpression.setExpression(requestBean.getExpression());
            mongoTemplate.insert(calculatorExpression);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "添加完成"));
        } catch (QuoteException ex) {
            throw new QuoteException(ex.getErrorMsg());
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差,添加失败");
        }
    }

    /**
     * 验证公式是否合法
     *
     * @param formula 数学公式
     * @return .
     */
    private boolean verifyFormula(String formula) throws QuoteException {
        formula = formula.replaceAll(" ", "");
        formula = formula.replaceAll("[+\\-*\\/]", "?");
        logger.info("公式格式化清理运算符后：" + formula);
        if (formula.endsWith("?")) {
            logger.info("公式校验不通过，以运算符结尾");
            return false;
        }
        if (formula.startsWith("?")) {
            logger.info("公式校验不通过，以运算符开始");
            return false;
        }
        String[] formulaArray = formula.split("\\?");
        CommonRespBean<List<Dictionary>> dictionaryData = dictionaryService.findFieldByType(propertiesUtils.getProperty("dictionary.type.calculator_variable"));
        List<String> nameList = dictionaryData.getData().stream().map(dictionary -> dictionary.getEnglishName()).collect(Collectors.toList());
        for (String s : formulaArray) {
            if ("".equals(s)) {
                logger.info("公式校验不通过，运算符连接");
                return false;
            }
            Boolean isNumberStr = s.matches("^(\\+)?\\d+(\\.\\d+)?$");
            if (isNumberStr) {
                continue;
            } else {
                if (!nameList.contains(s)) {
                    logger.info("公式校验不通过！{} 为不合法字符", s);
                    return false;
                }
            }
        }

        logger.info("公式校验通过");
        return true;
    }

    /**
     * 根据constructId查询施工项数量计算表达式
     *
     * @param constructId 数量计算表达式id
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean getConstructCalculationFormula(Long constructId) throws QuoteException {
        Properties props = propertiesUtils.buildProperties(
                new String[]{propertiesUtils.getProperty("construct.constructId")}, new Object[]{constructId});
        CalculatorExpression calculatorExpression = findPoByUniqueKey(props, CalculatorExpression.class);
        if (calculatorExpression == null) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "数据不存在"));
        }
        CalculationFormulaBean calculationFormulaBean = new CalculationFormulaBean();
        BeanUtils.copyProperties(calculatorExpression, calculationFormulaBean);
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), calculationFormulaBean);
    }


    /**
     * 更新施工项数量计算表达式
     *
     * @param requestBean 请求参数body
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean updateConstructCalculationFormula(CalculationFormulaBean requestBean) throws QuoteException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("calculatorExpression.expressionId")},
                    new Object[]{requestBean.getExpressionId()});
            CalculatorExpression calculatorExpression = findPoByUniqueKey(props, CalculatorExpression.class);
            if (calculatorExpression == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "施工项表达式不存在"));
            }
            if (!verifyFormula(requestBean.getExpression())) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.INPUT_ERROR, "公式校验不通过"));
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("calculatorExpression.expressionId"), requestBean.getExpressionId());
            Update update = new Update();
            if (requestBean.getConstructId() != null) {
                update.set(propertiesUtils.getProperty("construct.constructId"), requestBean.getConstructId());
            }
            update.set(propertiesUtils.getProperty("calculatorExpression.expression"), requestBean.getExpression());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, calculatorExpression.getClass());
            if (writeResult.getN() > 0) {
                logger.info("更新施工项数量计算表达式成功：{}", requestBean.toString());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新成功"));
            } else {
                logger.info("更新施工项数量计算表达式失败：{}", requestBean.toString());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.UPDATE_FAIL, "更新失败"));
            }
        } catch (QuoteException ex) {
            throw new QuoteException(ex.getErrorMsg());
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差,更新失败");
        }
    }

    /**
     * 删除施工项计算公式
     *
     * @param constructId 施工项id
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean deleteConstructCalculationFormula(Long constructId) throws QuoteException {
        try {
            Map<String, Object> valuesMap = Maps.newHashMap();
            valuesMap.put(propertiesUtils.getProperty("construct.constructId"), constructId);
            Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);

            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("construct.constructId")},
                    new Object[]{constructId});
            CalculatorExpression calculatorExpression = findPoByUniqueKey(props, CalculatorExpression.class);

            if(calculatorExpression == null){
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "施工项计算公式不存在"));
            }
            WriteResult writeResult = mongoTemplate.remove(basicQuery, CalculatorExpression.class);
            if (writeResult.getN() > 0) {
                logger.info("删除数量计算公式成功，constructId:{}", constructId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除成功"));
            } else {
                logger.info("删除数量计算公式失败，constructId:{}", constructId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL, "删除失败"));
            }
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差，删除异常");
        }
    }
}
