package com.juran.quote.bean.enums;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/9 17:02
 * @description
 */
public enum ConstructPriceTemplateEnum {

    DECORATION_TYPE(0, "装修类型"),
    PACKAGE_VERSION(1, "报价版本"),
    PACKAGE_TYPE(2, "报价类型"),
    CONSTRUCT_CODE(3, "施工项编码"),
    CUSTOMER_PRICE(4, "客户报价"),
    FOREMAN_PRICE(5, "施工队报价"),
    HOUSE_TYPE(6, "户型"),
    LIMIT(7, "是否限量"),
    LIMIT_AMOUNT(8, "限量值");

    private Integer index;
    private String filed;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    ConstructPriceTemplateEnum(Integer index, String filed) {
        this.index = index;
        this.filed = filed;
    }

}


