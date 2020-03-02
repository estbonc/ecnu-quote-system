package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "package_bak")
public class PackageBackup {

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

    /**
     * 删除时间
     */
    private Date removeTime;
}
