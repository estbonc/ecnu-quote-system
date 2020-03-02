package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Product implements Serializable {

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
    @Field(value = "pcode")
    private String pCode;

    /**
     * 商品名称
     */
    @Field(value = "pname")
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
     * bom中使用数量
     */
    private BigDecimal usedQuantity=BigDecimal.ZERO;

}
