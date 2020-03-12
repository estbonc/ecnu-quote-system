package com.juran.quote.service;

import com.google.common.collect.Maps;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.po.PackagePrice;
import com.juran.quote.bean.po.PackagePriceBackup;
import com.juran.quote.bean.po.PackageVersion;
import com.juran.quote.bean.request.PackagePriceRequestBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.exception.RemoveException;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.KeyManagerUtil;
import com.juran.quote.utils.LogAnnotation;
import com.juran.quote.utils.MDQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.swing.text.Keymap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PackagePriceService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private KeyManagerUtil keyManagerUtil;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @LogAnnotation
    public ServiceResponse createPackagePrice(PackagePriceRequestBean pkgPrice) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, pkgPrice.getPackageVersionId());
        valuesMap.put(CommonStaticConst.Package.HOUSE_TYPE, pkgPrice.getHouseType());
        Query query = MDQueryUtil.getDBObjectByValues(valuesMap);

        String message;
        try {
            if(mongoTemplate.exists(query, PackagePrice.class)){
                message = "套餐价格已经存在，无需重复创建";
                logger.info(message);
                return new ServiceResponse(false, message);
            }
            PackagePrice target = new PackagePrice();
            BeanUtils.copyProperties(pkgPrice, target);
            target.setPackagePriceId(keyManagerUtil.getUniqueId());
            mongoTemplate.insert(target);

        } catch(BeansException e){
            message = String.format("创建套餐价格操作异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        } catch (RuntimeException e) {
            message = String.format("创建套餐价格出现异常：%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        message = "套餐价格创建成功";
        return new ServiceResponse(true, message);
    }

    @LogAnnotation
    public ServiceResponse removePackagePrice(Long packagePriceId) throws PackageException {
        String message;

        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_PRICE_ID, packagePriceId);
        Query query = MDQueryUtil.getDBObjectByValues(valuesMap);
        PackagePrice price = mongoTemplate.findOne(query, PackagePrice.class);
        if(price == null) {
            message = String.format("套餐价格 id:%d 不存在或者已经被删除",packagePriceId);
            logger.info(message);
            return new ServiceResponse(true, message);
        }

        try {
            if(!backupPackagePrice(price)){
                message = "备份套餐价格失败";
                return new ServiceResponse(false, message);
            }
            mongoTemplate.remove(query, PackagePrice.class);
        } catch (RuntimeException e) {
            message = String.format("删除套餐价格操作异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        message = String.format("套餐价格 id:%d 删除成功",packagePriceId);
        logger.info(message);
        return new ServiceResponse(true, message);
    }

    private Boolean backupPackagePrice(PackagePrice packagePrice) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_PRICE_ID, packagePrice.getPackagePriceId());
        Query query = MDQueryUtil.getDBObjectByValues(valuesMap);
        String message;
        try {
            if(mongoTemplate.exists(query, PackagePriceBackup.class)){
                logger.info("套餐价格 id:%d 已经备份无需重复操作", packagePrice.getPackagePriceId());
                return true;
            }
            PackagePriceBackup priceBackup = new PackagePriceBackup();
            BeanUtils.copyProperties(packagePrice, priceBackup);
            priceBackup.setRemoveTime(new Date());
            mongoTemplate.insert(priceBackup);
        } catch (BeansException e) {
            message = String.format("备份套餐价格操作异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        } catch (RuntimeException e) {
            message = String.format("备份套餐价格操作异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }

    @LogAnnotation
    public boolean removePackagePriceByPackage(Long packageId) throws PackageException {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_ID).is(packageId));
        Query query = new Query(criteria);
        PackageVersion version = mongoTemplate.findOne(query, PackageVersion.class);
        if(version == null){
            logger.info("套餐版本之套餐 id{} 不存在", packageId);
            return true;
        }
        try {
            removePackagePriceByPkgVersion(version.getPackageVersionId());
        } catch (PackageException e) {
            throw new PackageException("删除套餐价格异常");
        }
        return true;
    }

    @LogAnnotation
    public ServiceResponse removePackagePriceByPkgVersion(Long pkgVersionId) throws PackageException {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where(CommonStaticConst.Package.PACKAGE_VERSION_ID).is(pkgVersionId));
        Query query = new Query(criteria);

        List<PackagePrice> priceList = mongoTemplate.find(query, PackagePrice.class);
        if(priceList == null){
            return new ServiceResponse(true, "套餐价格已删除，无需重复操作");
        }

        String message;
        try {
            for(PackagePrice price: priceList){
                backupPackagePrice(price);
            }
            mongoTemplate.findAllAndRemove(query, PackagePrice.class);
        } catch (RuntimeException e) {
            message = String.format("删除套餐价格操作异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException("删除套餐价格操作异常");
        }
        return new ServiceResponse(true, "套餐删除价格成功");
    }
}
