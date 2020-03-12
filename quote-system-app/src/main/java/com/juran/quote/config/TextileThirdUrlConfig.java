package com.juran.quote.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 3D方http地址url和参数类型
 * miniProductUrl：url
 * miniProductType:区分630商品参数
 * */
@Component
@ConfigurationProperties(prefix = "thirdUrl")
@Data
public class TextileThirdUrlConfig {
    private String miniProductUrl;
    private String miniProductType;
    private String allCategoryUrl;
    private String getDecorationCompanyUrl;
}
