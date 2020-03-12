package com.juran.quote.bean.enums;

/**
 * 主材基本信息
 */
public enum MainMaterialEnum {

    // 主数据中的主材类型
    GUO_MEN_SHI("threshold-stone", "过门石"),
    BI_ZHI("wallpaper", "壁纸"),
    CHU_GUI("cabinet", "整体橱柜"),
    MU_MEN("door", "木门"),
    QIANG_ZHUAN("wallTiles", "墙砖"),
    HUA_PIAN("decoration-tiles", "花片"),
    DI_ZHUAN("floorTiles", "地砖"),
    DA_DI_ZHUAN("Tiles", "大地砖"),
    FLOORBOARD("floorboard","地板"),

    MARBLEMOSAIC("marblemosaic","马赛克，大理石"),
    DOORSTONE("doorstone","过门石"),

    // 3d设计中的主材类型
    D3_QIANG_QI("wallpaint", "墙漆"),
    D3_SHI_GAO_XIAN("cornice", "石膏线"),
    D3_QIANG_ZHUAN("wallTiles", "墙砖"),
    D3_DI_ZHUAN("floorTiles", "地砖");

    private String code;
    private String name;

    MainMaterialEnum(String code, String name) {
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
