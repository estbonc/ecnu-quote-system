package com.ecnu.paper.quotesystem.service.support;

import java.math.BigDecimal;

/**
 * 施工项数量自动带出计算
 */
public interface ICiQuantityCalc {

    /**
     * 绑定计算参数
     */
    void setContext(AutoCiQuantityContext context);

    /**
     * 计算数量
     */
    BigDecimal getQuantity();

}
