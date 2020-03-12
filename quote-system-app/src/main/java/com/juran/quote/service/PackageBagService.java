package com.juran.quote.service;

import com.google.common.collect.Maps;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.po.PackageBag;
import com.juran.quote.bean.po.PackageBagBackup;
import com.juran.quote.bean.request.PackageBagRequestBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.exception.RemoveException;
import com.juran.quote.utils.CommonStaticConst;
import com.juran.quote.utils.KeyManagerUtil;
import com.juran.quote.utils.LogAnnotation;
import com.juran.quote.utils.MDQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

@Service
public class PackageBagService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    KeyManagerUtil keyManagerUtil;
    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    /**
     * 创建套餐大礼包
     *
     * @param bag
     * @return
     */
    @LogAnnotation
    public ServiceResponse createPackageBag(PackageBagRequestBean bag) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PACKAGE_VERSION_ID, bag.getPackageVersionId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        String message;
        try {
            if(mongoTemplate.exists(basicQuery, PackageBag.class)){
                message = "套餐礼包已存在无需创建";
                logger.info(message);
                return new ServiceResponse(true, message);
            }
            PackageBag target = new PackageBag();
            BeanUtils.copyProperties(bag, target);
            if(bag.getBagId() == null || bag.getBagId().equals(0)){
                target.setBagId(keyManagerUtil.getUniqueId());
            }
            target.setBagStatus(CommonStaticConst.Common.ENABLE_STATUS);
            target.setUpdateTime(new Date());
            mongoTemplate.insert(target);
        } catch (RuntimeException e) {
            message = String.format("创建套餐礼包出现异常：%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        message = "套餐礼包创建成功";
        return new ServiceResponse(true, message);
    }

    /**
     * 删除套餐大礼包
     *
     * @param bagId
     * @return
     */
    @LogAnnotation
    public ServiceResponse removePackageBag(Long bagId) throws PackageException {
        ServiceResponse serviceResponse;
        String message;

        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_BAG_ID, bagId);
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);

        PackageBag bag;
        try {
            bag = mongoTemplate.findOne(basicQuery, PackageBag.class);
        } catch (Exception e) {
            message = String.format("查询套餐礼包出现异常：%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }

        if(bag == null){
            message = String.format("套餐礼包id:%d 不存在或者已经被删除", bagId);
            logger.info(message);
            return new ServiceResponse(true, message);
        }

        try {
           if(!backupPackageBag(bag)){
               message = "备份套餐礼包失败";
               return new ServiceResponse(false, message);
           }
           mongoTemplate.remove(basicQuery, PackageBag.class);
        } catch (RuntimeException e) {
            message = String.format("删除套餐礼包出现异常%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        message = String.format("套餐礼包%d 删除成功", bagId);
        return new ServiceResponse(true, message);
    }

    private boolean backupPackageBag(PackageBag bag) throws PackageException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(CommonStaticConst.Package.PKG_BAG_ID, bag.getBagId());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        try {
            if(mongoTemplate.exists(basicQuery, PackageBagBackup.class)){
                logger.error("套餐礼包id{} 已经备份，无需重复操作", bag.getBagId());
                return true;
            }
            PackageBagBackup target = new PackageBagBackup();
            BeanUtils.copyProperties(bag, target);
            target.setBagStatus(CommonStaticConst.Common.DISABLE_STATUS);
            target.setRemoveTime(new Date());
            mongoTemplate.insert(target);
        } catch (RuntimeException e) {
            String message = String.format("备份套餐礼包出现异常：%s", e.getMessage());
            logger.error(message);
            throw new PackageException(message);
        }
        return true;
    }
}
