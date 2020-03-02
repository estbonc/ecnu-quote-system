package com.ecnu.paper.quotesystem.service;

import com.ecnu.paper.quotesystem.bean.enums.ChargeCalculationTypeEnums;
import com.ecnu.paper.quotesystem.bean.enums.ChargeRateBaseEnums;
import com.ecnu.paper.quotesystem.bean.exception.QuoteException;
import com.ecnu.paper.quotesystem.bean.po.ChargeTypePo;
import com.ecnu.paper.quotesystem.bean.request.ChargeTypeRequestBean;
import com.ecnu.paper.quotesystem.bean.response.ChargeTypeResponseBean;
import com.ecnu.paper.quotesystem.bean.response.CommonRespBean;
import com.ecnu.paper.quotesystem.bean.response.PageRespBean;
import com.ecnu.paper.quotesystem.utils.CommonStaticConst;
import com.ecnu.paper.quotesystem.utils.KeyManagerUtil;
import com.ecnu.paper.quotesystem.utils.PropertiesUtils;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/2 15:17
 * @description
 */
@Service
public class ChargeTypeService extends BaseService {


    @Autowired
    private PropertiesUtils propertiesUtils;

    @Autowired
    private KeyManagerUtil keyManagerUtil;

    /**
     * 条件查询自定义费用类型
     *
     * @param decorationCompany 装饰公司
     * @param createTime        创建时间
     * @param status            状态
     * @param offset            分页参数
     * @param limit             分页参数
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean getChargeType(String decorationCompany, Long createTime, Integer status, Integer offset, Integer limit) throws QuoteException {
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(decorationCompany))
            criteria.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        if (createTime != null) {
            criteria.and(propertiesUtils.getProperty("common.createTime")).lt(new Date(createTime + CommonStaticConst.Common.TIME_FOR_ONE_DAY)).gte(new Date(createTime));
        }
        criteria.and(propertiesUtils.getProperty("common.status")).is(status);
        PageRespBean<ChargeTypeResponseBean> responsePage = new PageRespBean<>();
        Query query = new Query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, propertiesUtils.getProperty("common.updateTime"))));
        List<ChargeTypeResponseBean> responseList = Lists.newArrayList();
        try {
            long count = mongoTemplate.count(query, ChargeTypePo.class);
            responsePage.setTotal(count);

            if (offset != null && limit != null) query.skip((offset - 1) * limit);
            if (limit != null) query.limit(limit);
            List<ChargeTypePo> chargeTypeList = mongoTemplate.find(query, ChargeTypePo.class);
            if (!CollectionUtils.isEmpty(chargeTypeList)) {
                responseList = chargeTypeList.stream().map(cp -> {
                    ChargeTypeResponseBean responseBean = new ChargeTypeResponseBean();
                    BeanUtils.copyProperties(cp, responseBean);
                    responseBean.setCalculationTypeCode(cp.getCalculationType());
                    ChargeCalculationTypeEnums calcuType = ChargeCalculationTypeEnums.getEnumByCode(cp.getCalculationType());
                    if (calcuType != null) {
                        responseBean.setCalculationType(calcuType.getName());
                    }
                    responseBean.setRateBaseCode(cp.getRateBase());
                    ChargeRateBaseEnums rateBase = ChargeRateBaseEnums.getEnumByCode(cp.getRateBase());
                    if (rateBase != null) {
                        responseBean.setRateBase(rateBase.getName());
                    }
                    return responseBean;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("查询自定义收费异常{}", e.getMessage());
            throw new QuoteException("查询自定义收费异常");
        }
        responsePage.setLimit(limit.intValue());
        responsePage.setOffset(offset.intValue());
        return new CommonRespBean(responseList, new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), responsePage);
    }

    /**
     * 创建自定义费用
     *
     * @param requestBean request参数
     * @return .
     */
    public CommonRespBean createChargeType(ChargeTypeRequestBean requestBean) throws QuoteException {
        try {
            if (existing(requestBean.getDecorationCompany(), requestBean.getChargeType())) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, String.format("自定义收费类型%s已存在", requestBean.getChargeType())));
            }
            ChargeTypePo chargeType = new ChargeTypePo();
            BeanUtils.copyProperties(requestBean, chargeType);
            chargeType.setChargeTypeId(keyManagerUtil.getUniqueId());
            chargeType.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            chargeType.setCreateTime(new Date());
            chargeType.setUpdateTime(chargeType.getCreateTime());
            mongoTemplate.insert(chargeType);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "创建自定义收费完成"));
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差,添加自定义收费失败");
        }
    }

    /**
     * 编辑自定义费用
     *
     * @param chargeTypeId id
     * @param requestBean  .
     * @return .
     * @throws QuoteException .
     */
    public CommonRespBean updateChargeType(Long chargeTypeId, ChargeTypeRequestBean requestBean) throws QuoteException {
        try {
            Properties queryArgs = new Properties();
            queryArgs.put(propertiesUtils.getProperty("chargeType.chargeTypeId"), chargeTypeId);
            ChargeTypePo chargeTypePo = findPoByUniqueKey(queryArgs, ChargeTypePo.class);
            if (chargeTypePo != null && !requestBean.getChargeType().equals(chargeTypePo.getChargeType())
                    && existing(requestBean.getDecorationCompany(), requestBean.getChargeType())) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, String.format("收费类型%s已存在", requestBean.getChargeType())));
            }

            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("chargeType.chargeTypeId"), chargeTypeId);
            Update update = new Update();
            if (requestBean.getRemark() != null) {
                update.set(propertiesUtils.getProperty("chargeType.remark"), requestBean.getRemark());
            }
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            update.set(propertiesUtils.getProperty("common.decorationCompany"), requestBean.getDecorationCompany());
            update.set(propertiesUtils.getProperty("chargeType.chargeType"), requestBean.getChargeType());
            update.set(propertiesUtils.getProperty("chargeType.calculationType"), requestBean.getCalculationType());
            if (requestBean.getAmount() != null) {
                update.set(propertiesUtils.getProperty("chargeType.amount"), requestBean.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            if (requestBean.getRate() != null) {
                update.set(propertiesUtils.getProperty("chargeType.rate"), requestBean.getRate().setScale(2, BigDecimal.ROUND_HALF_UP));
                update.set(propertiesUtils.getProperty("chargeType.rateBase"), requestBean.getRateBase());
            }

            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, ChargeTypePo.class);
            if (writeResult.getN() > 0) {
                logger.info("更新成功：{}", requestBean.toString());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新成功"));
            } else {
                logger.info("更新失败：{}", requestBean.toString());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.UPDATE_FAIL, "更新失败"));
            }
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差,更新失败");
        }
    }

    /**
     * 删除自定义费用类型
     *
     * @param chargeTypeId 自定义费用类型id
     * @return .
     */
    public CommonRespBean deleteChargeType(Long chargeTypeId) throws QuoteException {
        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("chargeType.chargeTypeId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{chargeTypeId, CommonStaticConst.Common.ENABLE_STATUS});
            ChargeTypePo chargeTypePo = findPoByUniqueKey(props, ChargeTypePo.class);
            if (chargeTypePo == null) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "数据不存在，无法删除"));
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("chargeType.chargeTypeId"), chargeTypeId);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("common.updateTime"), new Date());
            update.set(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.DISABLE_STATUS);
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, chargeTypePo.getClass());
            if (writeResult.getN() > 0) {
                logger.info("删除成功：{}", chargeTypeId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除成功"));
            } else {
                logger.info("删除失败：{}", chargeTypeId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL, "删除失败"));
            }
        } catch (Exception ex) {
            throw new QuoteException("服务器开小差，删除异常");
        }
    }


    /**
     * 校验自定义费用类型是否存在
     *
     * @param chargeType 费用类型
     * @return .
     */
    public CommonRespBean checkChargeType(String chargeType, String decorationCompany) throws QuoteException {
        try {
            if (existing(decorationCompany, chargeType)) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "自定义费用类型已存在"));
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "校验通过"));
            }
        } catch (RuntimeException e) {
            logger.error("校验自定义费用类型异常", e.getMessage());
            throw new QuoteException("校验自定义费用类型");
        }
    }


    private boolean existing(String decorationCompany, String chargeType) {
        Criteria criteria = new Criteria();
        criteria.and(propertiesUtils.getProperty("common.decorationCompany")).is(decorationCompany);
        criteria.and(propertiesUtils.getProperty("chargeType.chargeType")).is(chargeType);
        criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
        return mongoTemplate.exists(new Query(criteria), ChargeTypePo.class);
    }
}
