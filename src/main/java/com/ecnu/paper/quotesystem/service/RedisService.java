package com.ecnu.paper.quotesystem.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";


    @SuppressWarnings("rawtypes")

    public void setString(String key, String str) {
        stringRedisTemplate.opsForValue().set(key, str);
    }
    public void setObject(String key, Object object) {
        redisTemplate.opsForValue().set(key, object);
    }
    public Object getObject(String key) {
      return  redisTemplate.opsForValue().get(key);
    }

    public void setString(String key, String str,long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, str,timeout,timeUnit);
    }


    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * @param key     key值
     * @param timeout 过期时间
     * @param unit    过期时间单位
     * @return 是否设置成功
     * @methodDesc: 功能描述:(设置该key值的过期时间)
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * @param key key值
     * @return 离过期时间还有多少秒  不存在：-2 ,永久：-1，限时：实际的值
     * @methodDesc: 功能描述:(查询该key值离过期时间还有多少秒)
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * @methodDesc: 功能描述:(根据key删除)
     * @param: @param
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void setListStringRightPush(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public String getListStringleftPop(String key) {
        return (String) stringRedisTemplate.opsForList().leftPop(key);
    }

    public void setListStringLeftPush(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 向set添加元素
     *
     * @param key
     * @param value
     * @return
     */
    public Long setValueToSet(String key, String value) {

        return stringRedisTemplate.opsForSet().add(key, value);

    }

    /**
     * 删除set的指定元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long removeValueToSet(String key, String... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);

    }

    /**
     * 批量删除keu
     *
     * @param keys 需要传入的keys
     * @return
     */
    public void removeByKeys(List<String> keys) {
         stringRedisTemplate.delete(keys);
    }

    /**
     * 获取set的数据
     *
     * @param key
     * @return
     */
    public Set<String> getSetFromRedis(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 模糊删除
     * @param redisPre
     */
    public void deleteBatch(String redisPre){
        Set<String> keys = redisTemplate.keys(redisPre+ "*");
        redisTemplate.delete(keys);
    }

    /**
     * 删除整个map
     * @param redisPre
     */
    public void deleteHashBatch(String redisPre){
        Set<String> keys = redisTemplate.opsForHash().keys(redisPre);
        if(!CollectionUtils.isEmpty(keys)){
            redisTemplate.opsForHash().delete(redisPre,keys.toArray());
        }
    }

    /**
     * 插入整个map
     * @param objectKey
     * @param value
     */
    public void setHashValue(String objectKey, Map value){
        redisTemplate.opsForHash().putAll(objectKey,value);
    }


    /**
     * 查出map中单个数据
     * @param objectKey
     * @return
     */
    public Object getHashValue(String objectKey,String HashKey){
        return  redisTemplate.opsForHash().get(objectKey,HashKey);
    }
    /**
     * 查出map中所有的value
     * @param objectKey
     * @return
     */
    public List getAllHashValue(String objectKey){
        return  redisTemplate.opsForHash().values(objectKey);
    }

    /**
     * 查出整个map
     * @param objectKey
     * @return
     */
    public Map getAllHashMap(String objectKey){
        return  redisTemplate.opsForHash().entries(objectKey);
    }
//    /**
//     * 插入值并返回原先的值
//     * tip：设置失效时间会有一点误差，
//     * @param key
//     * @return
//     */
//    public String getSet(String key,String value,long seconds,TimeUnit timeUnit) {
//        Long oldExpire = this.getExpire(key);
//        String retResult = stringRedisTemplate.opsForValue().getAndSet(key, value);
//        if(oldExpire>=0){
//            this.expire(key,oldExpire,timeUnit);
//        }else{
//            this.expire(key,seconds,timeUnit);
//        }
//        return retResult;
//    }

    private static final Long RELEASE_SUCCESS = 1L;

//    /**

//    需要使用分布式锁时候需要注入Jedis！！！！
//
//    /**
//     * 使用redis当分布式锁
//     * @param key
//     * @return
//     */
//    public boolean getByLock(String key,String value,long seconds) {
//        Jedis jedis = jedisPool.getResource();
//        try {
//            String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds);
//            if(LOCK_SUCCESS.equals(result)){
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            returnResource(jedis);
//        }
//        return  false;
//    }

//     * 释放分布式锁
//     * @param
//     * @param lockKey 锁
//     * @param requestId 请求标识
//     * @return 是否释放成功
//     */
//    public  boolean releaseDistributedLock(String lockKey, String requestId) {
//        Jedis jedis = jedisPool.getResource();
//        try {
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
//            if (RELEASE_SUCCESS.equals(result)) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            returnResource(jedis);
//        }
//        return false;
//    }
//
//    public void returnResource(Jedis jedis) {
//        if (jedis != null) {
//            jedisPool.returnResourceObject(jedis);
//        }
//    }

}

