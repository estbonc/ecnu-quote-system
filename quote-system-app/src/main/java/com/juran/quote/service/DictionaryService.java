package com.juran.quote.service;

import com.alibaba.fastjson.JSON;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.Dictionary;
import com.juran.quote.bean.request.DictionaryBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.CacheUtil;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.PropertiesUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 10/09/2018 6:26 PM
 * @Description: 字典服务
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Service
public class DictionaryService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    private PropertiesUtils propertiesUtils;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    /**
     * 根据type查询字典值
     *
     * @param type
     * @return
     */
    public CommonRespBean<List<Dictionary>> findFieldByType(String type) throws QuoteException {

        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.Dictionary.TYPE, type);
            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            basicQuery.with(new Sort(new Sort.Order(Sort.Direction.ASC, CommonStaticConst.Dictionary.PRIORITY)));
            List<Dictionary> dictionaries = mongoTemplate.find(basicQuery, Dictionary.class);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), dictionaries);
        } catch (Exception e) {
            logger.error("type:{}查询字典值异常{}", type, e.getMessage());
            throw new QuoteException("查询字典值异常");
        }
    }

    /**
     * 根据type和code查询字典值
     *
     * @param type
     * @return
     */
    public CommonRespBean<Dictionary> findFieldByTypeAndCode(String type, String code) throws PackageException {

        try {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put(CommonStaticConst.Dictionary.TYPE, type);
            basicDBObject.put(CommonStaticConst.Dictionary.CODE, code);
            BasicQuery basicQuery = new BasicQuery(basicDBObject);
            Dictionary dictionary = mongoTemplate.findOne(basicQuery, Dictionary.class);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), dictionary);
        } catch (Exception e) {
            logger.error("type:{}查询字典值异常{}", type, e.getMessage());
            throw new PackageException("查询字典值异常");
        }
    }

    /**
     * 新增字典数据
     *
     * @param dictionaryBean
     * @return
     */
    public CommonRespBean insertDictionary(DictionaryBean dictionaryBean) throws PackageException {
        try {
            Dictionary dictionary = new Dictionary();
            BeanUtils.copyProperties(dictionaryBean, dictionary);
            mongoTemplate.insert(dictionary);
            cacheUtil.initDictionary();
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "新增成功"));
        } catch (DuplicateKeyException e) {
            logger.error("{}新增字典值异常{}", JSON.toJSONString(dictionaryBean), e.getMessage());
            throw new PackageException("字段重复");
        } catch (Exception e) {
            logger.error("{}新增字典值异常{}", JSON.toJSONString(dictionaryBean), e.getMessage());
            throw new PackageException("新增字典值异常");
        }
    }

    /**
     * 查询所有字典数据
     *
     * @return
     */
    public CommonRespBean<List<Dictionary>> queryAllFields() {
        List<Dictionary> allFields = mongoTemplate.findAll(Dictionary.class);
        logger.info("查询字典共{}条记录", allFields == null ? 0 : allFields.size());
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), allFields);
    }
}
