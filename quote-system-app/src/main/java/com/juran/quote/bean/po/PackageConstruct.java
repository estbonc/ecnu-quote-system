package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * 套餐下的施工项
 */
@Data
@Document(collection = "packageConstruct")
public class PackageConstruct {

    @Id
    private String _id;

    private Long packageConstructId;

    private Long packageVersionId;

    private String packageVersion;

    private String houseType;

    private Long constructId;

    private BigDecimal limitQuantity;

    private BigDecimal unitPrice;

}
