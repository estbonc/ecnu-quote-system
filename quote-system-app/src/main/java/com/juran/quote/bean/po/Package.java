package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "package")
public class Package {

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 有效状态
     */
    private Integer status;

}
