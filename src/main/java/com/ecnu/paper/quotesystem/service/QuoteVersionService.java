package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.ConstructPricePo;
import com.juran.quote.bean.po.QuoteVersionPo;
import com.juran.quote.bean.request.QueryQuoteVersionBean;
import com.juran.quote.bean.request.QuoteVersionBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.KeyManagerUtil;
import com.juran.quote.utils.PropertiesUtils;
import com.mongodb.BasicDBObject;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @Author: xiongtao
 * @Date: 09/10/2018 1:51 PM
 * @Description: 报价版本service
 * @Email: xiongtao@juran.com.cn
 */
@Service
public class QuoteVersionService extends BaseService{

    @Autowired
    private KeyManagerUtil keyManagerUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PropertiesUtils propertiesUtils;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);


    /**
     * 新增报价版本
     *
     * @param source
     * @return
     */
    public CommonRespBean createQuoteVersion(QuoteVersionBean source) throws PackageException {

        QuoteVersionPo quoteVersionPo = new QuoteVersionPo();
        try {
            Boolean exists = checkQuoteVersion(source);
            if (exists) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "报价版本已存在，请重新输入!!!"));
            }
            BeanUtils.copyProperties(source, quoteVersionPo);
            quoteVersionPo.setQuoteVersionId(keyManagerUtil.getUniqueId());
            quoteVersionPo.setCreateTime(new Date());
 			quoteVersionPo.setUpdateTime(quoteVersionPo.getCreateTime());
            quoteVersionPo.setEndTime(new Date(source.getEndTime().getTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY-1));
            quoteVersionPo.setStatus(CommonStaticConst.Common.ENABLE_STATUS);
            mongoTemplate.insert(quoteVersionPo);
        } catch (BeansException e) {
            logger.error("quoteVersion:{} --create quoteVersion error!!!{}", JSON.toJSONString(quoteVersionPo), e.getMessage());
            throw new PackageException("新增报价版本出现异常");
        } catch (DuplicateKeyException e1) {
            logger.error("quoteVersion:{} --create quoteVersion error!!!{}", JSON.toJSONString(quoteVersionPo), e1.getMessage());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_EXISTING, "报价版本已存在，请重新输入!!!"));
        } catch (RuntimeException e2) {
            logger.error("quoteVersion:{} --create quoteVersion error!!!{}", JSON.toJSONString(quoteVersionPo), e2.getMessage());
            throw new PackageException("新增报价版本出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "新增报价版本成功"));
    }

    /**
     * 校验报价版本是否存在
     *
     * @param source
     * @return
     */
    private Boolean checkQuoteVersion(QuoteVersionBean source) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());

        basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), source.getDecorationTypeId());

        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), source.getQuoteTypeId());

        basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionCode"), source.getQuoteVersionCode());

        basicDBObject.put(propertiesUtils.getProperty("quoteVersion.startTime"), new BasicDBObject("$gte", source.getStartTime())
                .append("$lt", new Date(source.getStartTime().getTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY)));
        basicDBObject.put(propertiesUtils.getProperty("quoteVersion.endTime"), new BasicDBObject("$gte", source.getEndTime()).
                append("$lt", new Date(source.getEndTime().getTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY)));
        basicDBObject.put(propertiesUtils.getProperty("quoteVersion.region"), source.getRegion());
        basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
        boolean exists = mongoTemplate.exists(new BasicQuery(basicDBObject), QuoteVersionPo.class);

        //    todo 门店现在没有
        // basicDBObject.put(propertiesUtils.getProperty("quoteVersion.stores"), source.getStore());

        return exists;


    }

    /**
     * 删除报价版本
     *
     * @param id
     * @param user
     * @return
     */
    public CommonRespBean deleteQuoteVersion(Long id, String user) throws QuoteException {

        try {
            Properties props = propertiesUtils.buildProperties(
                    new String[]{propertiesUtils.getProperty("quoteVersion.quoteVersionId"), propertiesUtils.getProperty("common.status")},
                    new Object[]{id, CommonStaticConst.Common.ENABLE_STATUS});
            ConstructPricePo constructPricePo = findPoByUniqueKey(props,
                    ConstructPricePo.class);
            if(constructPricePo != null){
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_IN_USE, "删除失败，报价版本已被使用!!!"));
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), id);
            Update update = new Update();
            update.set(CommonStaticConst.Common.STATUS, CommonStaticConst.Common.DISABLE_STATUS);
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, user);
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, QuoteVersionPo.class);
            if (writeResult.getN() > 0) {
                logger.info("quoteVersionId:{} --delete  quoteVersion success!!!", id);
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "报价版本不存在"));
            }
        } catch (Exception e) {
            logger.error("quoteVersionId:{} --delete  quoteVersion error:{}", id, e.getMessage());
            throw new QuoteException("删除报价版本出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "删除报价版本成功"));


    }

    /**
     * 更新报价版本
     *
     * @param source
     * @return
     */
    public CommonRespBean updateQuoteVersion(QuoteVersionBean source, Long id) throws PackageException {

        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), id);
            basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
            Update update = new Update();
            update.set(propertiesUtils.getProperty("quoteVersion.startTime"), source.getStartTime());
            update.set(propertiesUtils.getProperty("quoteVersion.endTime"), new Date(source.getEndTime().getTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY-1));
            update.set(propertiesUtils.getProperty("quoteVersion.region"), source.getRegion());
//            update.set(propertiesUtils.getProperty("quoteVersion.stores"), source.getStores());
            update.set(CommonStaticConst.Common.UPDATE_TIME, new Date());
            update.set(CommonStaticConst.Common.UPDATE_BY, source.getUpdateBy());
            WriteResult writeResult = mongoTemplate.updateMulti(new BasicQuery(basicDBObject), update, QuoteVersionPo.class);
            if (writeResult.getN() > 0) {
                logger.info("quoteVersionId:{} --update  quoteVersion success!!!", source.getQuoteVersionId());
            } else {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "报价版本不存在"));
            }
        } catch (Exception e) {
            logger.error("quoteVersionId:{} --update  quoteVersion error:{}", source.getQuoteVersionId(), e.getMessage());
            throw new PackageException("更新报价版本出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新报价版本成功"));

    }

    /**
     * 列表查询报价版本
     *
     * @param source
     * @return
     */
    public PageRespBean<QuoteVersionBean> queryQuoteVersionList(QueryQuoteVersionBean source) throws PackageException {
        PageRespBean<QuoteVersionBean> page = new PageRespBean<>();

        BasicDBObject basicDBObject = new BasicDBObject();
        try {
            if (StringUtils.isNotBlank(source.getDecorationCompany())) {
                basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), source.getDecorationCompany());
            }

            if (source.getDecorationTypeId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("decorationType.decorationTypeId"), source.getDecorationTypeId());

            }
            if (source.getQuoteVersionId() != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), source.getQuoteVersionId());

            }
            if (source.getStartTime() != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.startTime"),new BasicDBObject("$lt",new Date(source.getStartTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY))
                        .append("$gte",new Date(source.getStartTime())));

            }
            if (source.getEndTime() != null) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.endTime"),new BasicDBObject("$lt",new Date(source.getEndTime() + CommonStaticConst.Common.TIME_FOR_ONE_DAY))
                        .append("$gte",new Date(source.getEndTime())));
            }
            if (StringUtils.isNotBlank(source.getRegion())) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.region"), source.getRegion());

            }
            if (source.getStatus() != null) {
                basicDBObject.put(propertiesUtils.getProperty("common.status"), source.getStatus());

            }
            if (StringUtils.isNotBlank(source.getStore())) {
                basicDBObject.put(propertiesUtils.getProperty("quoteVersion.stores"), source.getStore());

            }
            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            basicQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC,propertiesUtils.getProperty("common.updateTime"))));
            long count = mongoTemplate.count(basicQuery, QuoteVersionPo.class);
            page.setTotal(count);
            basicQuery.skip((source.getOffset() - 1) * source.getLimit());
            basicQuery.limit(source.getLimit());

            List<QuoteVersionPo> quoteVersionPos = mongoTemplate.find(basicQuery, QuoteVersionPo.class);
            List<QuoteVersionBean> beans = Lists.newArrayList();
            if (count > 0) {
                beans = quoteVersionPos.stream().map(p -> {
                    QuoteVersionBean quoteVersionBean = new QuoteVersionBean();
                    BeanUtils.copyProperties(p, quoteVersionBean);
                    return quoteVersionBean;
                }).collect(Collectors.toList());
            }
            page.setOffset(source.getOffset());
            page.setLimit(source.getLimit());
            page.setData(beans);
        } catch (Exception e) {
            logger.error("query quoteVersion list error:{}", e.getMessage());
            throw new PackageException(e.getMessage());
        }
        return page;

    }

}
