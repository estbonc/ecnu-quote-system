package com.ecnu.paper.quotesystem.bean.enums;

/**
 * @Author: xiongtao
 * @Date: 28/09/2018 3:37 PM
 * @Description: 状态枚举
 * @Email: xiongtao@juran.com.cn
 */
public enum StatusEnum {
    DELETE(0,"删除"),
    OK(1,"正常");

    private Integer code;

    private String value;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    StatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

}
