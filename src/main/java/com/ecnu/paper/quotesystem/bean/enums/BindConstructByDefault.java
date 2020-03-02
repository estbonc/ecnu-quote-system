package com.ecnu.paper.quotesystem.bean.enums;

public enum BindConstructByDefault {
    YES(1),
    NO(0);

    BindConstructByDefault(Integer value) {
        this.value = value;
    }

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
