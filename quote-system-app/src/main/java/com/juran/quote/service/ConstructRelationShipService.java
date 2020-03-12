package com.juran.quote.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.juran.core.utils.http.HttpSendUtil;
import com.juran.core.utils.http.bean.HttpResponse;
import com.juran.quote.bean.dto.BindMaterial;
import com.juran.quote.bean.dto.BindRoom;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.ConstructRelationship;
import com.juran.quote.bean.po.HouseTypePo;
import com.juran.quote.bean.po.RoomPo;
import com.juran.quote.bean.quote.CategoryDto;
import com.juran.quote.bean.quote.ConstructMaterialBindDto;
import com.juran.quote.bean.quote.ConstructRoomBindDto;
import com.juran.quote.bean.request.ConstructRelationshipRequestBean;
import com.juran.quote.bean.request.HouseTypeBean;
import com.juran.quote.bean.request.QueryConstructRelationShipBean;
import com.juran.quote.bean.request.QueryHouseTypeBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.MaterialRelationShipRespBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.bean.response.SpaceRelationShipRespBean;
import com.juran.quote.config.TextileThirdUrlConfig;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.KeyManagerUtil;
import com.juran.quote.utils.PropertiesUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: xiongtao
 * @Date: 22/10/2018 5:57 PM
 * @Description: 施工项带出关系维护
 * @Email: xiongtao@juran.com.cn
 */
@Service
public class ConstructRelationShipService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PropertiesUtils propertiesUtils;

    @Autowired
    KeyManagerUtil keyManagerUtil;

    @Autowired
    PackageRoomService roomService;

    @Autowired
    TextileThirdUrlConfig thirdConfig;

    /**
     * 更新施工项带出关系
     *
     * @param source
     * @return
     */
    public CommonRespBean updateRelationShip(ConstructRelationshipRequestBean source) throws QuoteException {
        try {
            if (checkUpdate(source)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "带出关系维护冲突，请确定所有房型和指定房型只维护了一种！！！"));
            }
            BasicDBObject basicDBObject = buildBasicDbObject(source.getConstructId(), source.getDecorationCompany(), source.getQuoteTypeId(), source.getHouseTypeId());
            ConstructRelationship ship = mongoTemplate.findOne(new BasicQuery(basicDBObject), ConstructRelationship.class);
            //true: 已经维护了带出关系，更新 false:未维护带出关系需要新建带出关系
            if (ship == null) {
                ConstructRelationship relationship = buildInsertConstructRelationShip(source, source.getHouseTypeId());
                mongoTemplate.insert(relationship);
            } else {
                updateShip(ship, source, basicDBObject);
            }
        } catch (QuoteException e) {
            logger.error("更新施工项带出关系{}", e.getMessage());
            throw new QuoteException("更新施工项带出关系异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新带出关系成功"));

    }


    /**
     * 查询空间带出关系
     *
     * @param source
     * @return
     */
    public CommonRespBean findSpaceConstructRelationShip(QueryConstructRelationShipBean source) throws QuoteException {
        List<SpaceRelationShipRespBean> resp = null;
        try {
            resp = Lists.newArrayList();
            Set<Long> roomIds = getSpaceByHouseType(source.getDecorationCompany(), source.getHouseTypeId());
            buildRoomInfo(resp, roomIds);
            BasicDBObject basicDBObject = buildBasicDbObject(source.getConstructId(), source.getDecorationCompany(), source.getQuoteTypeId(), source.getHouseTypeId());
            ConstructRelationship queryResult = mongoTemplate.findOne(new BasicQuery(basicDBObject), ConstructRelationship.class);
            if (queryResult != null && !CollectionUtils.isEmpty(queryResult.getBindRooms())) {
                for (SpaceRelationShipRespBean bean : resp) {
                    for (BindRoom bindRoom : queryResult.getBindRooms()) {
                        if (bean.getRoomId().equals(bindRoom.getRoomId())) {
                            bean.setHasBind(CommonStaticConst.Construct.BINDING);
                        }
                    }
                }
            }
        } catch (QuoteException e) {
            logger.error("空间查询施工项带出关系异常{}", e.getMessage());
            throw new QuoteException("空间查询施工项带出关系异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), resp);

    }


    /**
     * 查询主材带出关系
     *
     * @param source
     * @return
     */
    public CommonRespBean findMaterialConstructRelationShip(QueryConstructRelationShipBean source) throws QuoteException {
        List<MaterialRelationShipRespBean> resps = null;
        try {
            resps = getAllCategory();
            BasicDBObject basicDBObject = buildBasicDbObject(source.getConstructId(), source.getDecorationCompany(), source.getQuoteTypeId(), source.getHouseTypeId());
            ConstructRelationship queryResult = mongoTemplate.findOne(new BasicQuery(basicDBObject), ConstructRelationship.class);
            if (queryResult != null && !CollectionUtils.isEmpty(queryResult.getBindMaterials())) {
                for (BindMaterial bindMaterial : queryResult.getBindMaterials()) {
                    for (MaterialRelationShipRespBean resp : resps) {
                        if (bindMaterial.getMaterialCategoryId().equals(resp.getMaterialCategoryId())) {
                            resp.setHasBind(CommonStaticConst.Construct.BINDING);
                            resp.setIsDefault(bindMaterial.getIsDefault());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("主材查询施工项带出关系异常{}", e.getMessage());
            throw new QuoteException("空间查询施工项带出关系异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), resps);
    }


    /**
     * 校验本次编辑请求是否允许
     * 规则：1.当用户未指定户型时，如果有制定的户型的带出关系维护则不允许
     * 2.当用户已指定户型时，如果未制定的户型的带出关系维护则不允许
     *
     * @param source
     * @return
     */
    private Boolean checkUpdate(ConstructRelationshipRequestBean source) {
        BasicDBObject basicDBObject = buildBasicDbObject(source.getConstructId(), source.getDecorationCompany(), source.getQuoteTypeId(), source.getHouseTypeId());
        String condition = "";
        if (source.getHouseTypeId() == null) {
            condition = "$ne";
        } else {
            condition = "$eq";
        }
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), new BasicDBObject(condition, null));
        // room 空间 material 主材
        if (CommonStaticConst.Construct.TYPE_ROOM.equals(source.getType())) {
            basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindRooms"), new BasicDBObject("$elemMatch", new BasicDBObject("$ne", null)));
        } else {
            basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindMaterials"), new BasicDBObject("$elemMatch", new BasicDBObject("$ne", null)));
        }
        Boolean exits = mongoTemplate.exists(new BasicQuery(basicDBObject), ConstructRelationship.class);
        logger.info("本次编辑施工项是否出现户型冲突：{}", exits);
        return exits;
    }

    /**
     * 根据装饰公司及户型获取空间
     *
     * @param decorationCompany
     * @param houseTypeId
     * @return
     * @throws QuoteException
     */
    private Set<Long> getSpaceByHouseType(String decorationCompany, Long houseTypeId) throws QuoteException {
        Set<Long> roomIds = Sets.newHashSet();
        if (houseTypeId == null) {
            List<HouseTypeBean> supportHouseType = getSupportHouseType(decorationCompany);
            supportHouseType.forEach(p -> {
                if (!CollectionUtils.isEmpty(p.getRooms())) {
                    roomIds.addAll(p.getRooms());
                }
            });
        } else {
            Properties properties = new Properties();
            properties.put(propertiesUtils.getProperty("houseType.houseTypeId"), houseTypeId);
            properties.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            HouseTypePo houseTypePo = findPoByUniqueKey(properties, HouseTypePo.class);
            if(houseTypePo != null) {
                roomIds.addAll(houseTypePo.getRooms());
            }
        }
        return roomIds;
    }

    /**
     * 把空间id装成相应的空间信息
     *
     * @param resp
     * @param roomIds
     */
    private void buildRoomInfo(List<SpaceRelationShipRespBean> resp, Set<Long> roomIds) {
        Properties properties = new Properties();
        roomIds.forEach(p -> {
            properties.clear();
            properties.put(propertiesUtils.getProperty("houseType.roomId"), p);
            try {
                RoomPo room = findPoByUniqueKey(properties, RoomPo.class);
                if (room != null) {
                    SpaceRelationShipRespBean res = new SpaceRelationShipRespBean();
                    res.setRoomId(room.getRoomId());
                    res.setRoomName(room.getRoomName());
                    res.setRoomType(room.getEnglishName());
                    res.setHasBind(CommonStaticConst.Construct.NOT_BINDING);
                    resp.add(res);
                }

            } catch (QuoteException e) {
                logger.error("通过roomId：{}查询空间信息失败", p);
            }
        });
    }

    /**
     * 组装施工项相关基础查询db
     *
     * @param constructId
     * @param decorationCompany
     * @param quoteTypeId
     * @return
     */
    private BasicDBObject buildBasicDbObject(Long constructId, String decorationCompany, Long quoteTypeId, Long houseTypeId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("construct.constructId"), constructId);
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), decorationCompany);
        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quoteTypeId);
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), houseTypeId);

        return basicDBObject;
    }

    /**
     * 获取主材类目信息
     *
     * @return
     */
    private List<MaterialRelationShipRespBean> getAllCategory() {
        List<MaterialRelationShipRespBean> resps = Lists.newArrayList();
        HttpResponse httpResponse = HttpSendUtil.sendHttpGet(thirdConfig.getAllCategoryUrl());
        if (httpResponse.getStatus() == HttpStatus.SC_OK) {
            if (StringUtils.isNotEmpty(httpResponse.getContent())) {
                JSONObject jsonObject = JSONObject.parseObject(httpResponse.getContent());
                JSONArray data = jsonObject.getJSONArray("data");
                List<CategoryDto> categoryDtos = data.toJavaList(CategoryDto.class);
                if (!CollectionUtils.isEmpty(categoryDtos)) {
                    for (CategoryDto dto : categoryDtos) {
                        getRespFromDtos(dto, resps);
                    }
                }

            } else {
                logger.info("从3d获取的类目信息接口调用成功但是没有数据!!!");
            }
        }
        logger.info("从3d获取的类目信息共：{}个", resps.size());
        return resps;
    }

    /**
     * 获取类目叶子节点
     *
     * @param dto
     * @param resps
     */
    private void getRespFromDtos(CategoryDto dto, List<MaterialRelationShipRespBean> resps) {
        if (!CollectionUtils.isEmpty(dto.getChildren())) {
            for (CategoryDto d : dto.getChildren()) {
                getRespFromDtos(d, resps);
            }
        } else {
            MaterialRelationShipRespBean bean = new MaterialRelationShipRespBean();
            bean.setMaterialCategoryId(dto.getId());
            bean.setMaterialCategoryName(dto.getName());
            bean.setHasBind(CommonStaticConst.Construct.NOT_BINDING);
            bean.setIsDefault(CommonStaticConst.Construct.NOT_DEFAULT);
            resps.add(bean);
        }
    }


    /**
     * 更新带出关系
     *
     * @param ship
     * @param source
     * @param basicDBObject
     * @throws QuoteException
     */
    private void updateShip(ConstructRelationship ship, ConstructRelationshipRequestBean source, BasicDBObject basicDBObject) throws QuoteException {
        processUpdateShip(ship, source.getSelectMaterialList(), source.getSelectRoomList());
        processRemoveShip(ship, source.getRemoveMaterialList(), source.getRemoveRoomList());
        Update update = new Update();
        update.set(propertiesUtils.getProperty("construct.bindMaterials"), ship.getBindMaterials());
        update.set(propertiesUtils.getProperty("construct.bindRooms"), ship.getBindRooms());
        update.set(propertiesUtils.getProperty("common.updateBy"), ship.getUpdateBy());
        update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
        WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, ConstructRelationship.class);
        if (writeResult.getN() > 0) {
            logger.info("{}：更新绑定关系成功", ship.getConstructRelationshipId());
        }
    }

    /**
     * 根据装饰公司获取支持的户型信息
     *
     * @param decorationCompany
     * @return
     * @throws QuoteException
     */
    private List<HouseTypeBean> getSupportHouseType(String decorationCompany) throws QuoteException {
        QueryHouseTypeBean query = new QueryHouseTypeBean();
        query.setDecorationCompany(decorationCompany);
        query.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
        PageRespBean<HouseTypeBean> response = roomService.queryHouseType(query);
        List<HouseTypeBean> data = response.getData();
        return data;
    }


    /**
     * 组装新增的施工项带出关系数据
     *
     * @param source
     * @return
     */
    private ConstructRelationship buildInsertConstructRelationShip(ConstructRelationshipRequestBean source, Long houseTypeId) throws QuoteException {
        ConstructRelationship relationship = new ConstructRelationship();
        relationship.setConstructRelationshipId(keyManagerUtil.getUniqueId());
        relationship.setConstructId(source.getConstructId());
        relationship.setConstructCode(source.getConstructCode());
        relationship.setDecorationCompany(source.getDecorationCompany());
        relationship.setHouseTypeId(houseTypeId);
        relationship.setQuoteTypeId(source.getQuoteTypeId());
        relationship.setCreateTime(new Date());
        relationship.setUpdateTime(relationship.getCreateTime());
        relationship.setUpdateBy(source.getUpdateBy());
        processUpdateShip(relationship, source.getSelectMaterialList(), source.getSelectRoomList());
        return relationship;
    }

    /**
     * 移除主材或者空间的带出关系
     */
    private void processRemoveShip(ConstructRelationship ship, List<ConstructMaterialBindDto> removeMaterialList, List<ConstructRoomBindDto> removeRoomList) {
        if (!CollectionUtils.isEmpty(removeMaterialList)) {
            List<BindMaterial> result = ship.getBindMaterials().stream().filter(p -> {
                for (ConstructMaterialBindDto c : removeMaterialList) {
                    if (p.getMaterialCategoryId().equals(c.getMaterialCategoryId())) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }).collect(Collectors.toList());
            ship.setBindMaterials(result);
        }
        if (!CollectionUtils.isEmpty(removeRoomList)) {
            List<BindRoom> result = ship.getBindRooms().stream().filter(p -> {
                for (ConstructRoomBindDto c : removeRoomList) {
                    if (p.getRoomId().equals(c.getRoomId())) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }).collect(Collectors.toList());
            ship.setBindRooms(result);
        }
    }

    /**
     * 更新主材或者空间的带出关系
     */
    private void processUpdateShip(ConstructRelationship ship, List<ConstructMaterialBindDto> selectMaterialList, List<ConstructRoomBindDto> selectRoomList) throws QuoteException {
        if (!CollectionUtils.isEmpty(selectMaterialList)) {
            for (ConstructMaterialBindDto dto : selectMaterialList) {
                Boolean exits = Boolean.FALSE;
                for (BindMaterial bindMaterial : ship.getBindMaterials()) {
                    if (dto.getMaterialCategoryId().equals(bindMaterial.getMaterialCategoryId())) {
                        logger.info("{}:主材带出关系已有，更新...", dto.getMaterialCategoryId());
                        bindMaterial.setIsDefault(dto.getIsDefault());
                        exits = Boolean.TRUE;
                    }
                }
                if (!exits) {
                    logger.info("{}:主材带出关系无，新增...", dto.getMaterialCategoryId());
                    BindMaterial bindMaterial = new BindMaterial();
                    BeanUtils.copyProperties(dto, bindMaterial);
                    ship.getBindMaterials().add(bindMaterial);
                }

            }
        }
        if (!CollectionUtils.isEmpty(selectRoomList)) {
            for (ConstructRoomBindDto dto : selectRoomList) {
                Set<Long> roomIds = getSpaceByHouseType(ship.getDecorationCompany(), ship.getHouseTypeId());
                if (!CollectionUtils.isEmpty(roomIds) && roomIds.contains(dto.getRoomId())) {
                    Boolean exits = Boolean.FALSE;
                    for (BindRoom bindRoom : ship.getBindRooms()) {
                        if (bindRoom.getRoomId().equals(dto.getRoomId())) {
                            logger.info("{}:空间带出关系已有，更新...", dto.getRoomId());
                            bindRoom.setRoomId(dto.getRoomId());
                            bindRoom.setEnglishName(dto.getRoomType());
                            bindRoom.setRoomName(dto.getRoomName());
                            exits = Boolean.TRUE;
                        }
                    }
                    if (!exits) {
                        logger.info("{}:空间带出关系无，新增...", dto.getRoomId());
                        BindRoom bindRoom = new BindRoom();
                        BeanUtils.copyProperties(dto, bindRoom);
                        bindRoom.setEnglishName(dto.getRoomType());
                        ship.getBindRooms().add(bindRoom);
                    }
                }
            }
        }


    }


}
