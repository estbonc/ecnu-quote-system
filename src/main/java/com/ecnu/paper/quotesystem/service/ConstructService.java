package com.ecnu.paper.quotesystem.service;

import com.ecnu.paper.quotesystem.bean.po.CalculatorExpression;
import com.ecnu.paper.quotesystem.bean.po.ConstructPo;
import com.ecnu.paper.quotesystem.bean.po.PackageConstruct;
import com.ecnu.paper.quotesystem.bean.po.PackageRoom;
import com.ecnu.paper.quotesystem.exception.PackageException;
import com.ecnu.paper.quotesystem.utils.CalObjectValueBean;
import com.ecnu.paper.quotesystem.utils.CommonStaticConst;
import com.ecnu.paper.quotesystem.utils.JepFormulaEvaluator;
import com.ecnu.paper.quotesystem.utils.MDQueryUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/28 10:30
 * @description
 */
@Service
public class ConstructService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<Construct> queryAllConstruct() {
        BasicDBObject basicDBObject = new BasicDBObject();
        return mongoTemplate.find(new BasicQuery(basicDBObject), Construct.class);
    }

    public List<ConstructPo> queryAllIndivConstruct() {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
        return mongoTemplate.find(new BasicQuery(basicDBObject), ConstructPo.class);
    }


    public PackageConstruct queryPackageConstructByVersionIdAndConstructId(Long packageVersionId, Long constructId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        valuesMap.put(CommonStaticConst.Construct.CONSTRUCT_ID, constructId);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        //排序
        basicQuery.with(new Sort(Sort.Direction.DESC, "limitQuantity"));
        List<PackageConstruct> pcList = mongoTemplate.find(basicQuery, PackageConstruct.class);
        if (!CollectionUtils.isEmpty(pcList)) {
            return pcList.get(0);
        }
        return null;
    }

    public PackageConstruct queryPackageConstructByVersionIdAndConstructIdAndHouseType(Long packageVersionId, String houseType, Long constructId) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, packageVersionId);
        valuesMap.put(CommonStaticConst.Construct.CONSTRUCT_ID, constructId);
        valuesMap.put("houseType", houseType);
        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        //排序
        basicQuery.with(new Sort(Sort.Direction.DESC, "limitQuantity"));
        List<PackageConstruct> pcList = mongoTemplate.find(basicQuery, PackageConstruct.class);
        if (!CollectionUtils.isEmpty(pcList)) {
            return pcList.get(0);
        }
        return null;
    }

    public List<PackageConstruct> queryPackageConstruct(Long packageVersionId, String houseType) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (packageVersionId != null) basicDBObject.put("packageVersionId", packageVersionId);
        if (!StringUtils.isEmpty(houseType)) basicDBObject.put("houseType", houseType);
        List<PackageConstruct> packageConstructList = mongoTemplate.find(new BasicQuery(basicDBObject), PackageConstruct.class);
        return packageConstructList;
    }

    public Set<Long> extractConstructByRoom(Long pkgVerisonId, String houseType, String roomType) {
        Set<Long> extractedConstruct = Sets.newHashSet();
        Criteria criteria = new Criteria();
        criteria.and("packageVersionId").is(pkgVerisonId);
        criteria.and("houseType").is(houseType);
        criteria.and("roomType").ne(roomType);
        Query query = new Query(criteria);
        List<PackageRoom> pkgRoomList = mongoTemplate.find(query, PackageRoom.class);
        if (!CollectionUtils.isEmpty(pkgRoomList)) {
            pkgRoomList.stream().forEach(room -> {
                room.getSelectedConstruct().stream().forEach(c -> extractedConstruct.add(c.getCid()));
            });
        }
        return extractedConstruct;
    }

    private CalculatorExpression loadFormulas(Long constructId) {
        Criteria criteria = new Criteria();
        criteria.and(propertiesUtils.getProperty("construct.constructId")).is(constructId);
        Query query = new Query(criteria);
        CalculatorExpression expression = mongoTemplate.findOne(query, CalculatorExpression.class);
        return expression;
    }

    public BigDecimal calConstruct(Long constructId, CalObjectValueBean valueBean) throws PackageException {
        CalculatorExpression expression = loadFormulas(constructId);
        if (expression == null) {
            return BigDecimal.ZERO;
        }
        String formula = expression.getExpression();
        JepFormulaEvaluator jep = new JepFormulaEvaluator(formula);
        try {
            jep.parse();
            jep.addVariables(valueBean);
            Double result = jep.evaluate();
            return new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (ParseException e) {
            throw new PackageException(String.format("解析公式错误：%s", formula));
        } catch (Exception e) {
            throw new PackageException(String.format("解析公式错误：%s", formula));
        }
    }

    /**
     * 根据code获取施工项
     *
     * @param constructCode
     * @return
     */
    public ConstructPo getConstructByCode(String constructCode) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("construct.constructCode"), constructCode);
        ConstructPo one = mongoTemplate.findOne(new BasicQuery(basicDBObject), ConstructPo.class);
        return one;
    }
}
