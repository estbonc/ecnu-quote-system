package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "packagePrice")
public class PackagePrice {

    @Id
    private String _id;

    /**
     * 套餐价格id
     */
    @Indexed(unique = true)
    private Long packagePriceId;
    /**
     * 关联套餐版本ID
     */
    private Long packageVersionId;

    /**
     * 房型
     */
    private String houseType;

    /**
     * 基本报价
     */
    private BigDecimal baseAmount;

    /**
     * 套餐内面积
     */
    private BigDecimal baseArea;

    /**
     * 超出面积的收费单价
     */
    private BigDecimal unitPrice;
}
