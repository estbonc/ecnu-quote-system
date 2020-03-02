package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 套餐下的施工项
 */
@Data
@Document(collection = "packageConstruct_bak")
public class PackageConstructBackup {

    private Long packageConstructId;

    private Long packageVersionId;

    private String erpVersion;

    private String houseType;

    private Long constructId;

    private BigDecimal limitQuantity;

    private BigDecimal unitPrice;

    private Date removeDate;

}
