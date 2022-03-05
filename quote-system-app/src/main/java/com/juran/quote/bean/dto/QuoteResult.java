package com.juran.quote.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteResult {
    // 总价
    private BigDecimal totalPrice;
    // 优惠总价
    private BigDecimal discountedTotalPrice;
    // 软装总价
    private BigDecimal softDecorationPrice;
    // 硬装总价
    private BigDecimal hardDecorationPrice;
    // 施工项总价
    private BigDecimal constructionTotalPrice;
    // 材料总价
    private BigDecimal materialTotalPrice;
    //单位平米价
    private BigDecimal unitPrice;
    //其他收费
    private BigDecimal extraPrice;

    //材料价格
    private BigDecimal hardMaterialPrice;
}
