package com.juran.quote.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.quote.bean.po.StorePo;
import com.juran.quote.utils.CacheUtil;
import com.juran.quote.utils.MDQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/28 10:50
 * @description
 */
@Service
public class StoreService extends BaseService{

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CacheUtil cacheUtil;

    public List<StorePo> queryAllStore(String region) {
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

    public String getNameByCode(String code) {
        StorePo po = cacheUtil.getStore(code);
        if (po==null) {
            logger.info("通过编码:{},无法获取门店信息.", code);
            return null;
        }
        return po.getName();
    }
}
