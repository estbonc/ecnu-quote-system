package com.juran.quote.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/28 12:50
 * @description
 */
@Component
@ConfigurationProperties(prefix = "quote")
@Data
public class QuoteConfig {

    /**
     * 预算套内面积最大值
     */
    private Integer budgetAreaMax;

    /**
     * 报价老房默认套餐
     */
    private Long defaultOldPackageId;

    /**
     * 报价新房默认套餐
     */
    private Long defaultNewPackageId;

    /**
     * 各个环境的tokenName,sjj_token_alpha,sjj_token_uat,sjj_token
     */
    private String tokenName;

    /**
     * 各个环境的tokenName,user_alpha,user_uat,user
     */
    private String user;

    /**
     * 报价分享url
     */
    private String shareUrl;

    /**
     * 个性化报价结果url
     */
    private String quoteResultUrl;
}
