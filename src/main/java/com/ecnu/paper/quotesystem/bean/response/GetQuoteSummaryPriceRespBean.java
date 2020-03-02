package com.ecnu.paper.quotesystem.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 报价汇总信息
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetQuoteSummaryPriceRespBean implements Serializable {
    private static final long serialVersionUID = 7392389250794014806L;
    /**
     * 总价
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 参考总价
     */
    private BigDecimal referenceTotalPrice = BigDecimal.ZERO;

    /**
     * 施工项总价
     */
    private BigDecimal totalCIPrice = BigDecimal.ZERO;

    /**
     * 硬装总价
     */
    private BigDecimal hardAssemblyPrice = BigDecimal.ZERO;

    /**
     * 硬装参考总价
     */
    private BigDecimal referenceHardAssemblyPrice = BigDecimal.ZERO;

    /**
     * 软装总价
     */
    private BigDecimal softPrice = BigDecimal.ZERO;

    /**
     * 软装参考总价
     */
    private BigDecimal referenceSoftPrice = BigDecimal.ZERO;

    /**
     * 总面积
     */
    private BigDecimal totalSize = BigDecimal.ZERO;

    /**
     * 单价
     */
    private BigDecimal basePrice = BigDecimal.ZERO;

    /**
     * 平米单价
     */
    private BigDecimal squareMetrePrice = BigDecimal.ZERO;

    /**
     * 超出面积单价
     */
    private BigDecimal overPrice = BigDecimal.ZERO;

    /**
     * 超出面积
     */
    private BigDecimal overSize = BigDecimal.ZERO;

    /**
     * 超出面积价钱
     */
    private BigDecimal overSizePrice = BigDecimal.ZERO;

    /**
     * 超出施工项价钱
     */
    private BigDecimal overCIPrice = BigDecimal.ZERO;

    /**
     * 套餐外施工项价钱
     */
    private BigDecimal outerCIPrice = BigDecimal.ZERO;

    /**
     * 远程费用
     */
    private BigDecimal remotePrice = BigDecimal.ZERO;

    private BigDecimal overBagPrice = BigDecimal.ZERO;

    /**
     * 套餐内-主材超量金额
     */
    private BigDecimal materialPrice = BigDecimal.ZERO;

    /**
     * 套餐外-主材金额
     */
    private BigDecimal outMaterialPrice = BigDecimal.ZERO;


}
