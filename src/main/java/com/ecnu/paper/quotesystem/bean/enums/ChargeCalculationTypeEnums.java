package com.ecnu.paper.quotesystem.bean.enums;

public enum ChargeCalculationTypeEnums {
    FIXED_CHARGE(1, "固定收费"),
    RATE_CHARGE(2, "按比例收费");

    private Integer code;
    private String name;

    private ChargeCalculationTypeEnums(Integer code, String name) {
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

    public static ChargeCalculationTypeEnums getEnumByCode(Integer code) {
        for(ChargeCalculationTypeEnums e:ChargeCalculationTypeEnums.values()){
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
