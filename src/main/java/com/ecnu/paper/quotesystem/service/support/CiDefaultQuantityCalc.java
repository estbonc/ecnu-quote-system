package com.ecnu.paper.quotesystem.service.support;

import java.math.BigDecimal;

/**
 * 默认施工项数量带出：0
 */
public class CiDefaultQuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        return BigDecimal.ZERO;
    }
}
