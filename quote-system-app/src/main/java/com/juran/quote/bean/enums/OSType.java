package com.juran.quote.bean.enums;

public enum OSType {
    WINDOWS("windows"),
    MAC("mac")
    ;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    OSType(String type) {
        this.type = type;
    }
}
