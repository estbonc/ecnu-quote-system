package com.juran.quote.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.core.exception.ParentException;
import com.juran.quote.bean.po.Brand;
import com.juran.quote.bean.po.SelectedMaterial;
import com.juran.quote.utils.MDQueryUtil;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class BrandService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MaterialService materialService;


    public void insert(Brand brand) throws ParentException {
        mongoTemplate.insert(brand);
    }

    public int delete(String id) {
        return 0;
    }

    public int update(Brand brand) {
        return 0;
    }

    public Brand getById(String id) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("uniqueId", Long.valueOf(id));
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        return mongoTemplate.findOne(basicQuery, Brand.class);
    }

    public List<Brand> queryBrandList(List<Long> idList) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("uniqueId", new BasicDBObject("$in", idList));
        return mongoTemplate.find(new BasicQuery(basicDBObject), Brand.class);
    }

    public List<Brand> queryBrandByMaterialId(Long materialId) {
        SelectedMaterial material = materialService.getById(materialId.toString());
        if (StringUtils.isEmpty(material)) {
            logger.info("主材数据不存在,主材ID:{}", materialId);
            return Collections.emptyList();
        }
        List<Long> brandIdList = material.getBrandList();
        return this.queryBrandList(brandIdList);
    }
}
