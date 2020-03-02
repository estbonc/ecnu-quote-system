package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.*;
import com.juran.quote.bean.request.HouseTypeBean;
import com.juran.quote.bean.request.PackageRoomRequestBean;
import com.juran.quote.bean.request.QueryHouseTypeBean;
import com.juran.quote.bean.request.RoomBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PackageRoomService extends BaseService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PackageService packageService;

    @Autowired
    private KeyManagerUtil keyManagerUtil;
    @Autowired
    private PropertiesUtils propertiesUtils;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @LogAnnotation
    public ServiceResponse createPackageRoom(PackageRoomRequestBean pkgRoom) throws PackageException {
        ServiceResponse serviceResponse;
        String retMessage;
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, pkgRoom.getPackageVersionId());
        valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, pkgRoom.getHouseType());
        valuesMap.put(CommonStaticConst.Package.ROOM_TYPE, pkgRoom.getRoomType());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        if (mongoTemplate.exists(basicQuery, PackageRoom.class)) {
            retMessage = "套餐空间已经存在，无需重复创建";
            logger.info(retMessage);
            return new ServiceResponse(true, retMessage);
        }

        PackageRoom target = new PackageRoom();
        BeanUtils.copyProperties(pkgRoom, target);
        target.setPackageRoomId(keyManagerUtil.getUniqueId());
        if (pkgRoom.getSelectedConstruct() != null) {
            List<SelectedConstruct> selectedConstructDTOList = pkgRoom.getSelectedConstruct()
                    .stream()
                    .map(constructDTO -> {
                        SelectedConstruct construct = new SelectedConstruct();
                        BeanUtils.copyProperties(constructDTO, construct);
                        return construct;
                    })
                    .collect(Collectors.toList());
            target.setSelectedConstruct(selectedConstructDTOList);
            target.setSelectedMaterial(pkgRoom.getSelectedMaterial());
        }
        try {
            mongoTemplate.insert(target);
        } catch (MongoException e) {
            String message = String.format("插入套餐空间出错， 异常信息%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        retMessage = "套餐空间创建成功";
        return new ServiceResponse(true, retMessage);
    }

    public ServiceResponse removePackageRoom(Long packageRoomId) throws PackageException {
        ServiceResponse serviceResponse;
        String retMessage;

        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_ROOM_ID, packageRoomId);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        List<PackageRoom> packageRooms = mongoTemplate.find(basicQuery, PackageRoom.class);
        if (packageRooms == null || packageRooms.size() == 0) {
            retMessage = String.format("套餐空间已经被删除：无需重复操作%d", packageRoomId);
            logger.info(retMessage);
            return new ServiceResponse(true, retMessage);
        }

        try {
            backupPackageRoom(packageRooms.get(0));
            mongoTemplate.remove(basicQuery, PackageRoom.class);
        } catch (MongoException e) {
            String message = String.format("删除套餐空间出现异常:%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }

        retMessage = String.format("套餐空间%d删除成功", packageRoomId);
        logger.info(retMessage);
        return new ServiceResponse(true, retMessage);
    }

    private Boolean backupPackageRoom(PackageRoom packageRoom) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_ROOM_ID, packageRoom.getPackageRoomId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        if (mongoTemplate.exists(basicQuery, PackageRoomBackup.class)) {
            logger.info("套餐空间已经备份， 无需重复操作{}", packageRoom.getPackageRoomId());
            return true;
        }
        PackageRoomBackup packageRoomBackup = new PackageRoomBackup();
        BeanUtils.copyProperties(packageRoom, packageRoomBackup);
        packageRoomBackup.setRemoveDate(new Date());
        try {
            mongoTemplate.insert(packageRoomBackup);
        } catch (RuntimeException e) {
            String message = String.format("备份套餐空间操作出错：%d", packageRoom.getPackageRoomId());
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    @LogAnnotation
    public Boolean removePackageRoomByPackage(Long packageId) throws PackageException {
        PackageVersion version = packageService.getPackageVersionByPackage(packageId);
        try {
            if (version != null) {
                removePackageRoomByPkgVersion(version.getPackageVersionId());
            }
        } catch (PackageException e) {
            throw new PackageException("删除套餐空间异常");
        }
        return true;
    }

    @LogAnnotation
    public ServiceResponse removePackageRoomByPkgVersion(Long pkgVersionId) throws PackageException {
        Criteria criatira = new Criteria();
        criatira.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(pkgVersionId));
        Query query = new Query(criatira);
        List<PackageRoom> packageRooms = mongoTemplate.find(query, PackageRoom.class);
        if (CollectionUtils.isEmpty(packageRooms)) {
            return new ServiceResponse(true, "套餐空间已删除，无需重复操作");
        }
        for (PackageRoom room : packageRooms) {
            backupPackageRoom(room);
        }
        try {
            mongoTemplate.findAllAndRemove(query, PackageRoom.class);
        } catch (RuntimeException e) {
            String message = String.format("批量删除套餐空间异常：%s", e.getMessage());
            logger.error(message);
            throw new PackageException("批量删除套餐空间异常");
        }
        return new ServiceResponse(true, "套餐空间已删除成功");

    }

    public List<String> queryAllRoomTypes(String houseType) {
        List<String> roomList;
        if (StringUtils.isBlank(houseType)) {
            roomList = mongoTemplate.getCollection("packageRoom").distinct("roomType");
        } else {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put("houseType", houseType);
            roomList = mongoTemplate.getCollection("packageRoom").distinct("roomType", basicDBObject);
        }

        logger.info("空间去重个数:{}", roomList.size());
        return roomList;
    }

    public PackageRoom querySinglePackageRoom(PackageRoomRequestBean requestBean) {
        Map<String, Object> querParams = Maps.newHashMap();
        querParams.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, requestBean.getPackageVersionId());
        querParams.put(CommonStaticConst.Package.HOUSE_TYPE, requestBean.getHouseType());
        querParams.put(CommonStaticConst.Package.ROOM_TYPE, requestBean.getRoomType());

        Query query = MDQueryUtil.getDBObjectByValues(querParams);
        PackageRoom room = mongoTemplate.findOne(query, PackageRoom.class);
        return room;
    }

    /**
     * 新增空间
     *
     * @param source
     * @return
     */
    public CommonRespBean createRoom(RoomBean source) throws PackageException {
        RoomPo roomPo = new RoomPo();
        try {
            BeanUtils.copyProperties(source, roomPo);
            roomPo.setRoomId(keyManagerUtil.getUniqueId());
            roomPo.setCreateTime(new Date());
            roomPo.setUpdateTime(roomPo.getCreateTime());
            roomPo.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            roomPo.setUpdateBy(source.getUpdateBy());
            mongoTemplate.insert(roomPo);
        } catch (BeansException e) {
            logger.error("room:{} --create room error!!!", JSON.toJSONString(roomPo), e.getMessage());
            throw new PackageException("新增空间出现异常");
        } catch (DuplicateKeyException e1) {
            logger.error("room:{} --create room error!!!", JSON.toJSONString(roomPo), e1.getMessage());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "空间已存在，请重新输入!!!"));
        } catch (RuntimeException e2) {
            logger.error("room:{} --create room error!!!", JSON.toJSONString(roomPo), e2.getMessage());
            throw new PackageException("新增空间出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "新增空间成功"));
    }

    /**
     * 删除空间
     *
     * @param id
     * @return
     */
    public CommonRespBean deleteRoom(Long id, String user) throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.HouseRoom.ROOMS, id);
            basicDBObject.put(CommonStaticConst.Common.STATUS, CommonStaticConst.Common.ENABLE_STATUS);
            boolean isUsed = mongoTemplate.exists(new BasicQuery(basicDBObject), HouseTypePo.class);
            if (isUsed) {
                logger.info("roomId:{} --the room is in use!!!", id);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "该空间已被使用，不能删除"));
            }
            basicDBObject.clear();
            basicDBObject.put(CommonStaticConst.HouseRoom.ROOM_ID, id);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.DISABLE_STATUS);
            update.set(propertiesUtils.getProperty("common.updateBy"), user);
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            WriteResult result = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, RoomPo.class);
            if (result.getN() > 0) {
                logger.info("roomId:{} --delete room success!!!", id);
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "空间不存在"));
            }
        } catch (Exception e) {
            logger.error("roomId:{} --delete  room error:{}", id, e.getMessage());
            throw new PackageException("删除空间出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除空间成功"));
    }

    /**
     * 查询所有空间
     *
     * @return
     */
    public CommonRespBean<List<RoomPo>> queryAllRoom() throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            List<RoomPo> allRooms = mongoTemplate.find(new BasicQuery(basicDBObject), RoomPo.class);
            List<RoomBean> roomBeans = Lists.newArrayList();
            if (allRooms != null) {
                roomBeans = allRooms.stream().map(p -> {
                    RoomBean roomBean = new RoomBean();
                    BeanUtils.copyProperties(p, roomBean);
                    return roomBean;
                }).collect(Collectors.toList());
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询空间成功"), roomBeans);
        } catch (Exception e) {
            logger.error("--query allRoom  error :{}", e.getMessage());
            throw new PackageException("查询所有空间出现异常");
        }
    }

    /**
     * 新建户型
     *
     * @param source
     * @return
     */
    public CommonRespBean createHouseType(HouseTypeBean source) throws PackageException {

        try {
            HouseTypePo houseTypePo = new HouseTypePo();
            BeanUtils.copyProperties(source, houseTypePo);
            houseTypePo.setHouseTypeId(keyManagerUtil.getUniqueId());
            houseTypePo.setCreateTime(new Date());
            houseTypePo.setUpdateTime(houseTypePo.getCreateTime());
            houseTypePo.setUpdateBy(source.getUpdateBy());
            houseTypePo.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            mongoTemplate.insert(houseTypePo);
        } catch (BeansException e) {
            logger.error("houseType:{} --create  houseType error :{}", JSON.toJSONString(source), e.getMessage());
            throw new PackageException("新建户型出现异常");
        } catch (DuplicateKeyException e1) {
            logger.error("houseType:{} --create  houseType error :{}", JSON.toJSONString(source), e1.getMessage());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "已有正常状态的该户型！！！"));
        } catch (RuntimeException e2) {
            logger.error("houseType:{} --create  houseType error :{}", JSON.toJSONString(source), e2.getMessage());
            throw new PackageException("新建户型出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "新建户型成功"));

    }

    /**
     * 删除户型
     *
     * @param id 户型id
     * @return
     */
    public CommonRespBean deleteHouseType(Long id, String user) throws PackageException {
        try {
            Boolean isUsed = checkHouseTypeUsed(id);
            if (isUsed) {
                logger.info("houseTypeId:{} --the houseType is in use!!!", id);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "该户型已被使用，不能删除"));
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.HouseRoom.HOUSE_TYPE_ID, id);
            Update update = new Update();
            update.set(CommonStaticConst.Common.STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, user);
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, HouseTypePo.class);
            if (writeResult.getN() > 0) {
                logger.info("houseTypeId:{} --delete  houseType success!!!", id);
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "户型不存在"));
            }
        } catch (Exception e) {
            logger.error("houseTypeId:{} --delete  houseType error:{}", id, e.getMessage());
            throw new PackageException("删除户型出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除户型成功"));
    }

    /**
     * 校验户型是否正在使用
     *
     * @param id
     * @return
     */
    private Boolean checkHouseTypeUsed(Long id) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), id);
        Boolean exists = mongoTemplate.exists(new BasicQuery(basicDBObject), ConstructRelationship.class);
        return exists;
    }

    /**
     * 更新户型
     *
     * @param id     户型id
     * @param source 户型数据
     * @return
     */
    public CommonRespBean updateHouseType(Long id, HouseTypeBean source) throws PackageException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.HouseRoom.HOUSE_TYPE_ID, id);
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            HouseTypePo result = mongoTemplate.findOne(new BasicQuery(basicDBObject), HouseTypePo.class);
            Set<Long> rooms = result.getRooms().stream().filter(p -> {
                if (source.getRooms().contains(p)) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }).collect(Collectors.toSet());
            for (Long roomId : rooms) {
                if (checkSpaceDelete(roomId, source.getDecorationCompany())) {
                    Properties properties = new Properties();
                    properties.put(propertiesUtils.getProperty("houseType.roomId"), roomId);
                    RoomPo room = findPoByUniqueKey(properties, RoomPo.class);
                    String msg = String.format("施工项关系带出绑定了:%s，无法删除", room.getRoomName());
                    logger.info(msg);
                    return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, msg));
                }
            }
            Update update = new Update();
            update.set(CommonStaticConst.HouseRoom.ROOMS, source.getRooms());
            update.set(CommonStaticConst.HouseRoom.REMARK, source.getRemark());
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, source.getUpdateBy());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, HouseTypePo.class);
            if (writeResult.getN() > 0) {
                logger.info("houseTypeId:{} update houseType success result :{}", id, writeResult.getN());
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "户型不存在"));
            }
        } catch (Exception e) {
            logger.error("houseType:{} --update  houseType error:{}", JSON.toJSONString(source), e);
            throw new PackageException("更新户型出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新户型成功"));

    }

    /**
     * 校验户型中的空间是否有被删除的
     *
     * @return
     */
    private Boolean checkSpaceDelete(Long roomId, String decorationCompany) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), decorationCompany);
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindRooms.roomId"), roomId);
        boolean exists = mongoTemplate.exists(new BasicQuery(basicDBObject), ConstructRelationship.class);
        return exists;
    }

    /**
     * 列表条件查询户型
     *
     * @param source
     * @return
     */
    public PageRespBean<HouseTypeBean> queryHouseType(QueryHouseTypeBean source) throws QuoteException {
        PageRespBean<HouseTypeBean> page = new PageRespBean<>();
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (StringUtils.isNotBlank(source.getDecorationCompany())) {
                basicDBObject.put(CommonStaticConst.HouseRoom.DECORATION_COMPANY, source.getDecorationCompany());
            }
            if (source.getStatus() != null) {
                basicDBObject.put(CommonStaticConst.Common.STATUS, source.getStatus());
            }
            if (source.getCreateTime() != null) {
                basicDBObject.put(CommonStaticConst.Common.CREATE_TIME, new BasicDBObject("$lt", new Date(source.getCreateTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY))
                        .append("$gte", new Date(source.getCreateTime())));
            }
            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            basicQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
            long count = mongoTemplate.count(basicQuery, HouseTypePo.class);

            page.setTotal(count);
            basicQuery.skip((source.getOffset() - 1) * source.getLimit());
            basicQuery.limit(source.getLimit());
            List<HouseTypePo> houseTypePos = mongoTemplate.find(basicQuery, HouseTypePo.class);
            List<HouseTypeBean> houseTypeBeans = Lists.newArrayList();
            if (count > 0) {
                houseTypeBeans = houseTypePos.stream().map(houseTypePo -> {
                    HouseTypeBean houseTypeBean = new HouseTypeBean();
                    BeanUtils.copyProperties(houseTypePo, houseTypeBean);
                    return houseTypeBean;
                }).collect(Collectors.toList());
            }
            page.setOffset(source.getOffset());
            page.setLimit(source.getLimit());
            page.setData(houseTypeBeans);
        } catch (Exception e) {
            logger.error("query houseType list error:{}", e.getMessage());
            throw new QuoteException(e.getMessage());
        }
        return page;

    }

    /**
     * 根据houseType 查 QuoteTypePo
     *
     * @param name 房型houseType
     * @return .
     * @throws QuoteException .
     */
    public HouseTypePo getHouseTypeByName(String name) throws QuoteException {
        try {
            Criteria criteria = new Criteria();
            criteria.and(propertiesUtils.getProperty("houseType.houseType")).is(name);
            criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
            Query query = new Query(criteria);
            HouseTypePo houseTypePo = mongoTemplate.findOne(query, HouseTypePo.class);
            return houseTypePo;
        } catch (Exception e) {
            logger.error("查询房型异常{}", e.getMessage());
            throw new QuoteException("查询房型异常");
        }
    }

    public List<RoomPo> getRoomsByIds(List<Long> ids) throws QuoteException {
        try {
            Criteria criteria = new Criteria();
            criteria.and(propertiesUtils.getProperty("houseType.roomId")).in(ids);
            criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
            Query query = new Query(criteria);
            return mongoTemplate.find(query, RoomPo.class);
        } catch (Exception e) {
            throw new QuoteException("查询房间信息出处");
        }
    }

    /**
     * 根据户型id查询户型
     *
     * @return .
     * @throws QuoteException .
     */
    public HouseTypePo getHouseTypeById(Long id) throws QuoteException {
        try {
            Criteria criteria = new Criteria();
            criteria.and(propertiesUtils.getProperty("houseType.houseTypeId")).is(id);
            criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
            Query query = new Query(criteria);
            HouseTypePo houseTypePo = mongoTemplate.findOne(query, HouseTypePo.class);
            return houseTypePo;
        } catch (Exception e) {
            logger.error("查询房型异常{}", e.getMessage());
            throw new QuoteException("查询房型异常");
        }
    }
}
