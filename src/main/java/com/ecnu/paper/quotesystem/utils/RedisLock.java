package com.ecnu.paper.quotesystem.utils;

import com.ecnu.paper.quotesystem.config.RedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/15 11:03
 * @description
 */
@Component
public class RedisLock {

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    private static final String LOCK = "lock:";

    @Autowired
    private RedisProperties redisConfig;

    @Resource(name = "redisTemplate")
    protected RedisTemplate redisTemplate;

    /**
     * 阻塞线程
     *
     * @param lockKey .
     * @return boolean
     */
    public boolean acquire(final String lockKey) {
        return acquire(lockKey, true, getSpinNum(0));
    }

    /**
     * 阻塞线程
     *
     * @param lockKey .
     * @return boolean
     */
    public boolean acquire(final String lockKey, Integer spinNum) {
        return acquire(lockKey, true, getSpinNum(spinNum));
    }

    /**
     * 非阻塞线程
     *
     * @param lockKey .
     * @return boolean
     */
    public boolean acquireNoSpin(final String lockKey) {
        return acquire(lockKey, false, getSpinNum(0));
    }


    /**
     * 校验是否有权限执行
     * 获取锁代表可执行，获取后立即释放
     *
     * @param lockKey .
     * @return boolean
     */
    public boolean acquireAndRelease(final String lockKey) {
        if (acquire(lockKey)) {
            release(lockKey);
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取spinNum
     *
     * @param num
     * @return
     */
    private Integer getSpinNum(Integer num) {
        if (num <= 0) {
            return redisConfig.getSpinNum();
        }
        return num;
    }

    /**
     * 获取锁
     *
     * @param lockKey .
     * @param isSpin  .
     * @param spinNum .
     * @return .
     */
    public boolean acquire(final String lockKey, final boolean isSpin, final Integer spinNum) {
        try {
            if (!redisConfig.isEnable()) {
                return true;
            }
            return (Boolean) redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) {
                    try {
                        long spinNumTemp = spinNum;
                        while (spinNumTemp > 0) {
                            if (connection.setNX(SerializationUtils.serialize(getLockKey(lockKey)), SerializationUtils.serialize(lockKey))) {
                                connection.expire(SerializationUtils.serialize(getLockKey(lockKey)), redisConfig.getLockFailureTime());
                                log.info("lockKey:{} ,获取锁 ，自动失效时间 lockFailureTime:{}", lockKey, redisConfig.getLockFailureTime());
                                return true;
                            } else {
                                if (!isSpin) {// 如果不需要自旋获取锁，直接返回结果
                                    return false;
                                }
                            }
                            spinNumTemp -= 1;
                            Thread.sleep(redisConfig.getSpinThreadTime());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return false;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取LockKey
     *
     * @param key .
     * @return .
     */
    private String getLockKey(String key) {
        return LOCK + key;
    }

    /**
     * 释放锁
     *
     * @param keys .
     */
    public void release(final String... keys) {
        try {
            if (!redisConfig.isEnable()) {
                return;
            }
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) {
                    for (String key : keys) {
                        connection.del(SerializationUtils.serialize(getLockKey(key)));
                    }
                    log.info("lockKey: " + keys + " ,释放锁!");
                    return true;
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
