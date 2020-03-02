package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.util.Date;

@Data
public class BatchUploadConstructRespBean {
    private Long constructId;

    private String constructCategory;

    private String constructItem;

    private String constructCode;

    private String constructName;

    private String unitCode;

    private String desc;

    private String assitSpec;

    private String standard;

    private String sourceOfStandard;

    private Integer status;

    private String remark;

    private String decorationCompany;

    private Date createTime;

    private Date updateTime;

    private String updateBy;

    private Integer result;

    private String message;
}
