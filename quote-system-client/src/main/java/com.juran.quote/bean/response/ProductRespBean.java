package com.juran.quote.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRespBean implements Serializable {

    private static final long serialVersionUID = -918192872856027100L;
    /**
     * 商品ID
     */
    private Long pid;

    /**
     * 商品-套餐关系:1_2
     */
    private String pidWithPackageId;

    /**
     * 商品编码    SKU
     */
    private String pCode;

    /**
     * 商品名称
     */
    private String pName;

    /**
     * 规格
     */
    private String spec;

    /**
     * 型号
     */
    private String model;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 选中状态
     */
    private Boolean selected = Boolean.FALSE;

    /**
     * 是否超市产品
     */
    private String coho;

    /**
     * 主数据对应的sku
     */
    private String mdmSku;

    /**
     * 主材使用的数量,默认为0
     */
    private BigDecimal usedQuantity = BigDecimal.ZERO;

}
