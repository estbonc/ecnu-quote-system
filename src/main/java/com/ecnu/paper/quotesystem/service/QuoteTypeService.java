package com.ecnu.paper.quotesystem.service;

import com.ecnu.paper.quotesystem.bean.exception.QuoteException;
import com.ecnu.paper.quotesystem.bean.po.ConstructPricePo;
import com.ecnu.paper.quotesystem.bean.po.ConstructRelationship;
import com.ecnu.paper.quotesystem.bean.po.DecorationTypePo;
import com.ecnu.paper.quotesystem.bean.po.QuoteTypePo;
import com.ecnu.paper.quotesystem.bean.request.QuoteTypeBean;
import com.ecnu.paper.quotesystem.bean.response.CommonRespBean;
import com.ecnu.paper.quotesystem.bean.response.PageRespBean;
import com.ecnu.paper.quotesystem.bean.response.QuoteTypeVo;
import com.ecnu.paper.quotesystem.utils.CommonStaticConst;
import com.ecnu.paper.quotesystem.utils.KeyManagerUtil;
import com.ecnu.paper.quotesystem.utils.PropertiesUtils;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * author:chenpeng
 * 报价类型逻辑实现类
 */
@Service
public class QuoteTypeService extends BaseService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    KeyManagerUtil keyManagerUtil;

    @Autowired
    PropertiesUtils propertiesUtils;

    @Autowired
    DecorationTypeService decorationTypeService;

    private final Logger logger = LoggerFactory.getLogger(QuoteTypeService.class);

    public CommonRespBean getQuoteTypes(String decorationCompany,
                                        Long decorationTypeId,
                                        String quoteType,
                                        String code,
                                        Integer status,
                                        Integer limit,
                                        Integer offSet) throws QuoteException {
        try {
            PageRespBean<QuoteTypeVo> responsePage = new PageRespBean<>();
            Criteria criatira = new Criteria();
            if (StringUtils.isNotBlank(decorationCompany)) {
                criatira.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
            }
            if (decorationTypeId != null) {
                criatira.and(propertiesUtils.getProperty("decorationType.decorationTypeId")).is(decorationTypeId);
            }
            if (StringUtils.isNotBlank(quoteType)) {
                criatira.and(propertiesUtils.getProperty("common.name")).is(quoteType);
            }
            if (StringUtils.isNotBlank(code)) {
                criatira.and(propertiesUtils.getProperty("quoteType.code")).is(code);
            }
            if (status != null) {
                criatira.and(propertiesUtils.getProperty("common.status")).is(status);
            }
            Query query = new Query(criatira);
            query.with(new Sort(new Sort.Order(Sort.Direction.DESC, propertiesUtils.getProperty("common.updateTime"))));
            long count = mongoTemplate.count(query, QuoteTypePo.class);
            responsePage.setTotal(count);

            if (offSet != null && limit != null) query.skip((offSet - 1) * limit);
            if (limit != null) query.limit(limit);

            List<QuoteTypePo> quoteTypes = mongoTemplate.find(query, QuoteTypePo.class);
            List<QuoteTypeVo> quoteTypeVos = new ArrayList<>();
            Properties props = new Properties();
            for (QuoteTypePo qt : quoteTypes) {
                QuoteTypeVo qtvo = new QuoteTypeVo();
                BeanUtils.copyProperties(qt, qtvo);
                props.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), qt.getDecorationTypeId());
                props.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
                DecorationTypePo dtp = decorationTypeService.findPoByUniqueKey(
                        props,
                        DecorationTypePo.class);
                if (dtp == null) {
                    continue;
                }
                qtvo.setDecorationType(dtp.getName());
                qtvo.setDecorationTypeId(dtp.getDecorationTypeId());
                quoteTypeVos.add(qtvo);
            }
            responsePage.setLimit(limit.intValue());
            responsePage.setOffset(offSet.intValue());
            return new CommonRespBean(quoteTypeVos, new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                    "获取报价类型成功"),
                    responsePage);
        } catch (Exception e) {
            logger.error("查询报价类型异常{}", e.getMessage());
            throw new QuoteException("查询报价类型异常");
        }
    }

    public CommonRespBean updateQuoteType(Long quoteTypeId, QuoteTypeBean request, String user) throws QuoteException {
        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            if (quoteTypeId != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quoteTypeId);
            } else {
                basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), request.getDecorationCompany());
                basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), request.getDecorationTypeId());
                basicDBObject.put(propertiesUtils.getProperty("common.name"), request.getName());
            }
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.name"), request.getName());
            update.set(propertiesUtils.getProperty("quoteType.code"), request.getCode());
            update.set(propertiesUtils.getProperty("common.description"), request.getDescription());
            update.set(propertiesUtils.getProperty("common.updateBy"), user);
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject),
                    update, QuoteTypePo.class);
            if (writeResult.getN() > 0) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                        "更新报价类型成功"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING,
                        "更新报价类型失败"));
            }
        } catch (Exception e) {
            logger.error("更新装修类型异常{}", e.getMessage());
            throw new QuoteException("更新报价类型异常");
        }
    }

    private boolean ifExisting(String decorationCompany, Long decorationTypeId, String name) {
        Criteria criatira = new Criteria();
        criatira.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        criatira.and(propertiesUtils.getProperty("decorationType.decorationTypeId")).is(decorationTypeId);
        criatira.and(propertiesUtils.getProperty("common.name")).is(name);
        criatira.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
        Query query = new Query(criatira);
        return mongoTemplate.exists(query, QuoteTypePo.class);
    }

    public CommonRespBean createQuoteType(QuoteTypeBean request, String userName) throws QuoteException {
        try {
            if (ifExisting(request.getDecorationCompany(), request.getDecorationTypeId(), request.getName())) {
                logger.info("报价类型{}已存在", request.getName());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING,
                        "报价类型已存在!"));
            }

            QuoteTypePo target = new QuoteTypePo();
            BeanUtils.copyProperties(request, target);
            target.setUpdateBy(userName);
            target.setCreateTime(new Date());
            target.setUpdateTime(target.getCreateTime());
            target.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            target.setQuoteTypeId(keyManagerUtil.getUniqueId());
            mongoTemplate.insert(target);
        } catch (Exception e) {
            logger.error(String.format("创建报价类型异常{}", e.getMessage()));
            throw new QuoteException("创建报价类型异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                "创建报价类型成功"));
    }

    public CommonRespBean deleteQuoteTypes(Long id, String userName) throws QuoteException {
        StringBuilder message = new StringBuilder();
        try {
            if (hasDependency(id)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL,
                        "报价类型正在使用，不能删除！请检查后操作"));
            }

            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.clear();
            basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), id);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.updateBy"), userName);
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            update.set(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.DISABLE_STATUS);
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject),
                    update,
                    QuoteTypePo.class);
            if (writeResult.getN() > 0) {
                message.append(String.format("报价类型%s删除成功;", id));
            } else {
                message.append(String.format("报价类型%s删除失败;", id));
            }
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, message.toString()));
        } catch (Exception e) {
            logger.info(message.toString());
            logger.error("删除报价类型异常{}", e.getMessage());
            throw new QuoteException("删除报价类型异常");
        }
    }

    private boolean hasDependency(Long quoteTypeId) throws QuoteException {
        Properties props = propertiesUtils.buildProperties(
                new String[]{propertiesUtils.getProperty("quoteType.quoteTypeId"), propertiesUtils.getProperty("common.status")},
                new Object[]{quoteTypeId, CommonStaticConst.Common.ENABLE_STATUS});
        try {
            ConstructPricePo constructPricePo = findPoByUniqueKey(props, ConstructPricePo.class);
            if (constructPricePo != null) {
                return true;
            }
            props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("quoteType.quoteTypeId")},
                    new Object[]{quoteTypeId});
            ConstructRelationship constructRelationship = findPoByUniqueKey(props, ConstructRelationship.class);
            if (constructRelationship != null) {
                return true;
            }
        } catch (QuoteException e) {
            logger.error("删除报价类型查询错误{}", e.getMessage());
            throw e;
        }
        return false;
    }
}
