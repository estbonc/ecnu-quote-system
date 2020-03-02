package com.ecnu.paper.quotesystem.service.support;

import java.math.BigDecimal;

/**
 * 其他02	成品保护（保护膜）	平米		每个合同都有此项，价钱为面积（套内建筑面积） * 单价
 */
public class CiQita02QuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        return super.getQuantityAsInnerArea();
    }
}
