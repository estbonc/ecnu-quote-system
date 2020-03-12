package com.juran.quote.service;

import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.ConstructPricePo;
import com.juran.quote.bean.po.DecorationTypePo;
import com.juran.quote.bean.po.QuoteTypePo;
import com.juran.quote.bean.request.DecorationTypeRequestBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.KeyManagerUtil;
import com.juran.quote.utils.LogAnnotation;
import com.juran.quote.utils.PropertiesUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DecorationTypeService extends BaseService{
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    KeyManagerUtil keyManagerUtil;

    @Autowired
    PropertiesUtils propertiesUtils;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    private boolean ifExisting(String decorationCompany, String name) {
        Criteria criatira = new Criteria();
        criatira.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        criatira.and(propertiesUtils.getProperty("common.name")).is(name);
        criatira.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
        Query query = new Query(criatira);
        return mongoTemplate.exists(query, DecorationTypePo.class);
    }

    @LogAnnotation
    public CommonRespBean createDecorationType(DecorationTypeRequestBean requestBean,
                                               String user) throws QuoteException {
        try {
            if (ifExisting(requestBean.getDecorationCompany(), requestBean.getName())) {
                logger.info("装修类型{}已存在", requestBean.getName());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING,
                        "装修类型已存在!"));
            }

            DecorationTypePo target = new DecorationTypePo();
            BeanUtils.copyProperties(requestBean, target);
            target.setUpdateBy(user);
            target.setCreateTime(new Date());
            target.setUpdateTime(target.getCreateTime());
            target.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            target.setDecorationTypeId(keyManagerUtil.getUniqueId());
            mongoTemplate.insert(target);
        } catch (Exception e) {
            logger.error(String.format("创建装修类型异常{}", e.getMessage()));
            throw new QuoteException("创建装修类型异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                "创建装修类型成功"));
    }

    @LogAnnotation
    public CommonRespBean updateDecorationType(Long id,
                                               DecorationTypeRequestBean requestBean,
                                               String userFromCookie) throws QuoteException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (id != null) {
                basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), id);
            } else {
                basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), requestBean.getDecorationCompany());
                basicDBObject.put(propertiesUtils.getProperty("common.name"), requestBean.getName());
            }
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.name"), requestBean.getName());
            update.set(propertiesUtils.getProperty("common.description"), requestBean.getDescription());
            update.set(propertiesUtils.getProperty("common.updateBy"), userFromCookie);
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject),
                    update, DecorationTypePo.class);
            if (writeResult.getN() > 0) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                        "更新装修类型成功"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING,
                        "更新装修类型失败"));
            }
        } catch (Exception e) {
            logger.error("更新装修类型异常{}", e.getMessage());
            throw new QuoteException("更新装修类型异常");
        }
    }

    public CommonRespBean deleteDecorationType(Long docerationTypeId,
                                               String userFromCookie) throws QuoteException {
        StringBuilder message = new StringBuilder();
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("decorationType.decorationTypeId"),propertiesUtils.getProperty("common.status")},
                    new Object[]{docerationTypeId, CommonStaticConst.Common.ENABLE_STATUS});
            QuoteTypePo quoteTypePo = findPoByUniqueKey(props,QuoteTypePo.class);
            if (quoteTypePo != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL,
                        "有报价类型正在使用，不能删除！请检查后操作"));
            }

            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("decorationType.decorationTypeId"),propertiesUtils.getProperty("common.status")},
                    new Object[]{docerationTypeId, CommonStaticConst.Common.ENABLE_STATUS});
            ConstructPricePo constructPricePo = findPoByUniqueKey(props,ConstructPricePo.class);
            if (constructPricePo != null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL,
                        "有施工项价格正在使用，不能删除！请检查后操作"));
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.clear();
            basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"),
                    docerationTypeId);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.updateBy"), userFromCookie);
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            update.set(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.DISABLE_STATUS);
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject),
                    update,
                    DecorationTypePo.class);
            if (writeResult.getN() > 0) {
                message.append(String.format("装修类型%s删除成功;", docerationTypeId));
            } else {
                message.append(String.format("装修类型%s删除失败;", docerationTypeId));
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message.toString()));
        } catch (Exception e) {
            logger.info(message.toString());
            logger.error("删除装修类型异常{}", e.getMessage());
            throw new QuoteException("删除装修类型异常");
        }
    }

    public CommonRespBean getDecorationTypes(String decorationCompany, String decorationType,
                                             Integer status) throws QuoteException {
        try {
            Criteria criatira = new Criteria();
            if (!StringUtils.isEmpty(decorationCompany)) {
                criatira.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
            }
            if (!StringUtils.isEmpty(decorationType)) {
                criatira.and(propertiesUtils.getProperty("common.name")).is(decorationType);
            }
            if (status != null) {
                criatira.and(propertiesUtils.getProperty("common.status")).is(status);
            }
            Query query = new Query(criatira);
            query.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
            List<DecorationTypePo> responseList = mongoTemplate.find(query, DecorationTypePo.class);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                    "获取装修类型成功"),
                    responseList);
        } catch (Exception e) {
            logger.error("查询装修类型异常{}", e.getMessage());
            throw new QuoteException("查询装修类型异常");
        }
    }

}
