package com.ecnu.paper.quotesystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/15 11:05
 * @description
 */
@Component
@Data
@ConfigurationProperties(prefix = "redis", ignoreUnknownFields = true)
public class RedisProperties {
    /**
     * 是否激活分布式锁
     */
    private boolean enable;

    /**
     * 锁失效时间
     */
    private Integer lockFailureTime;

    /**
     * 自旋次数
     */
    private Integer spinNum;

    /**
     * 重试时间
     */
    private Integer spinThreadTime;
}
