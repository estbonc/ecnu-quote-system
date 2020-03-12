package com.juran.quote.bean.enums;

public enum ChargeRateBaseEnums {
    BASE_ON_MATERIAL_PRICE(1,"材料金额"),
    BASE_ON_CONSTRUCT_PRICE(2, "施工金额"),
    BASE_ON_MATERIAL_CONSTRUCT_PRICE(3, "材料加施工项金额")
    ;
    private Integer code;
    private String name;

    private ChargeRateBaseEnums(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ChargeRateBaseEnums getEnumByCode(Integer code) {
        for(ChargeRateBaseEnums e:ChargeRateBaseEnums.values()){
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
