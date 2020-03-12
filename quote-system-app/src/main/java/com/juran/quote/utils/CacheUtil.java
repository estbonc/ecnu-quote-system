package com.juran.quote.utils;

import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.enums.RedisPreEnum;
import com.juran.quote.bean.po.Construct;
import com.juran.quote.bean.po.ConstructPo;
import com.juran.quote.bean.po.Dictionary;
import com.juran.quote.bean.po.StorePo;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.ConstructService;
import com.juran.quote.service.DictionaryService;
import com.juran.quote.service.RedisService;
import com.juran.quote.service.StoreService;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内存数据
 */
@Component
public class CacheUtil implements InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @Autowired
    ConstructService constructService;

    @Autowired
    StoreService storeService;

    @Autowired
    RedisService redisService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    PropertiesUtils propertiesUtils;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("系统初始化数据...");
        this.initConstruct();
        this.initStore();
        this.initDictionary();
        this.initIndividualConstruct();
    }

    /**
     * 初始化施工项
     */
    public void initConstruct() {
        redisService.deleteHashBatch(RedisPreEnum.CONSTRUCT.getPre());
        List<Construct> constructList = constructService.queryAllConstruct();
        HashMap<String, Construct> stringConstructHashMap = new HashMap<>();
        int i=0;
        for (Construct c : constructList) {
            stringConstructHashMap.put(c.getConstructId().toString(),c);
            i++;
        }
        redisService.setHashValue(RedisPreEnum.CONSTRUCT.getPre(),stringConstructHashMap);
        logger.info("初始化施工项缓存成功,Map大小:{}", i);
    }

    /**
     * 初始化施工项
     */
    public void initIndividualConstruct() {
        redisService.deleteHashBatch(RedisPreEnum.INDIVIDUALCONSTRUCT.getPre());
        List<ConstructPo> constructList = constructService.queryAllIndivConstruct();
        HashMap<String, ConstructPo> stringConstructHashMap = new HashMap<>();
        int i=0;
        for (ConstructPo c : constructList) {
            stringConstructHashMap.put(c.getConstructCode(),c);
            i++;
        }
        redisService.setHashValue(RedisPreEnum.INDIVIDUALCONSTRUCT.getPre(),stringConstructHashMap);
        logger.info("初始化个性化施工项缓存成功,Map大小:{}", i);
    }

    /**
     * 初始化门店
     */
    public void initStore() {
        redisService.deleteBatch(RedisPreEnum.STORE.getPre());
        int i=0;
        HashMap<String, StorePo> storeMap = new HashMap<>();
        List<StorePo> storeList = storeService.queryAllStore("");
        if(!CollectionUtils.isEmpty(storeList)){
            logger.info("初始化门店List成功,storeList大小:{}", storeList.size());
            for (StorePo po : storeList) {
                storeMap.put(po.getCode(),po);
                i++;
            }
            redisService.setHashValue(RedisPreEnum.STORE.getPre(),storeMap);
        }

        logger.info("初始化门店缓存成功,storeMap大小:{}", i);
    }

    /**
     * 初始化字典
     */
    public void initDictionary() {
        redisService.deleteBatch(RedisPreEnum.DICTIONARY.getPre());
        HashMap<String, Dictionary> dictionaryMap = new HashMap<>();
        CommonRespBean<List<Dictionary>> listCommonRespBean = dictionaryService.queryAllFields();
        List<Dictionary> dictionaryList = listCommonRespBean.getData();
        if(CollectionUtils.isNotEmpty(dictionaryList)){
            dictionaryList.forEach(p->{
                dictionaryMap.put(p.getType()+":"+p.getCode(),p);
            });
            redisService.setHashValue(RedisPreEnum.DICTIONARY.getPre(),dictionaryMap);
            logger.info("初始化字典List成功,storeList大小:{}", dictionaryList.size());
        }else{
            logger.info("初始化字典缓存失败");
        }


    }

    /**
     * 获取施工项
     *
     * @return
     */
    public Construct getCachedConstruct(String constructId) {
        return (Construct)redisService.getHashValue(RedisPreEnum.CONSTRUCT.getPre(),constructId);
    }

    public ConstructPo getCachedIndiviConstruct(String constructCode) {
        ConstructPo constructPo = (ConstructPo)redisService.getHashValue(RedisPreEnum.INDIVIDUALCONSTRUCT.getPre(),constructCode);
        if(constructPo==null){
            logger.info("缓存穿透,查询数据库========>{}",constructCode);
            ConstructPo constructByCode = constructService.getConstructByCode(constructCode);
            if(constructByCode==null){
                logger.info("数据库无此施工项========>{}",constructCode);
                return null;
            }
            logger.info("更新缓存========>");
            initIndividualConstruct();
            return constructByCode;
        }
        return constructPo;
    }

    /**
     * 获取所有缓存施工项
     * @return
     */
    public Map getAllConstructMap() {
        Map<String,Construct> allConstruct = redisService.getAllHashMap(RedisPreEnum.CONSTRUCT.getPre());
        return allConstruct;
    }
    /**
     * 获取所有缓存门店信息
     * @return
     */
    public Map getAllStore() {
        Map<String,Construct> allStore = redisService.getAllHashMap(RedisPreEnum.STORE.getPre());
        return allStore;
    }
    /**
     * 获取单个门店方法
     *
     * @return
     */
    public StorePo getStore(String code) {
        return (StorePo)redisService.getHashValue(RedisPreEnum.STORE.getPre(),code);
    }

    /**
     * 获取门店的list
     * @return
     */
    public List<StorePo> getStoreList() {
        return redisService.getAllHashValue(RedisPreEnum.STORE.getPre());
    }
    /**
     * 获取字典字段
     * @return
     */
    public Dictionary getDictionary(String type,String code) {
        Dictionary dictionary=null;
        try {
            dictionary= (Dictionary) redisService.getHashValue(RedisPreEnum.DICTIONARY.getPre(), type + ":" + code);
            if(dictionary==null){
                CommonRespBean resp = dictionaryService.findFieldByTypeAndCode(type, code);
                dictionary = (Dictionary)resp.getData();
                if(dictionary!=null){
                    logger.info("缓存穿透重新初始化缓存数据=========>");
                    initDictionary();
                }
            }
        } catch (PackageException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    /**
     * 获取字典字段的名称
     * @return
     */
    public String getDictionaryName(String code) {
        Dictionary dictionary = getDictionary(propertiesUtils.getProperty("dictionary.type.unit"), code);
        if(dictionary!=null){
            return dictionary.getName();
        }
        return null;
    }
    /**
     * 获取施工项的名称
     * @return
     */
    public String getCachedIndiviConstructName(String constructCode) {
        ConstructPo cachedIndiviConstruct = getCachedIndiviConstruct(constructCode);
        if(cachedIndiviConstruct!=null){
           return cachedIndiviConstruct.getConstructName();
        }
        return null;
    }
    /**
     * 获取施工项的名称
     * @return
     */
    public Long getCachedIndiviConstructId(String constructCode) {
        ConstructPo cachedIndiviConstruct = getCachedIndiviConstruct(constructCode);
        if(cachedIndiviConstruct!=null){
            return cachedIndiviConstruct.getConstructId();
        }
        return null;
    }



}
