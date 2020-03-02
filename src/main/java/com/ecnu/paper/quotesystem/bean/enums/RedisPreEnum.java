package com.ecnu.paper.quotesystem.bean.enums;

/**
 * redis前缀枚举
 */
public enum RedisPreEnum {

    MATERIAL_PRICE("MATERIAL_PRICE:","主材价格前缀"),
    CONSTRUCT("CONSTRUCT","施工项前缀"),
    STORE("STORE","门店前缀"),
    INDIVIDUALCONSTRUCT("INDIVIDUALCONSTRUCT","个性化施工项前缀"),
    DICTIONARY("DICTIONARY","字典前缀");


    private String pre;
    private String desc;

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RedisPreEnum(String pre, String desc) {
        this.pre = pre;
        this.desc = desc;
    }
}
