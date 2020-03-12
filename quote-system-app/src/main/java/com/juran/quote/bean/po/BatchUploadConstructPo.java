package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "batchUploadConstruct")
public class BatchUploadConstructPo {

    private String batchNum;

    @Indexed(unique = true)
    private Long batchResultId;

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

    private Integer logStatus;
}
