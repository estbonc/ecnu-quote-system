package com.ecnu.paper.quotesystem.bean.enums;

/**
 * 施工项基本信息
 */
public enum ConstructionItemEnum {

    DEFAULT("-1","未识别施工项"),

    TUSHI_19("233", "涂饰19"),

    TUSHI_08("56", "涂饰08"),

    DINGMIAN_36("130", "顶面36"),

    QITA_02("144", "其他02"),

    QITA_03("145", "其他03"),

    QITA_05("147", "其他05"),

    WATERPROOF_AND_OTHER_01("556", "防水及其它01"),

    INSTALL_09("553","安装09"),

    FLOOR_22("437","地面22"),

    WATERPROOF_AND_OTHER_04("559", "防水及其它04"),

    WATERPROOF_AND_OTHER_05("560", "防水及其它05"),

    WATERPROOF_AND_OTHER_06("561", "防水及其它06")



    ;

    private String code;
    private String name;

    ConstructionItemEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
