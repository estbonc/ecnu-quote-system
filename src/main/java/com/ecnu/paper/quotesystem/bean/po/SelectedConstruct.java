package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SelectedConstruct implements Serializable {

    /**
     * 施工项ID
     */
    private Long cid;

    /**
     * 已使用的数量,默认数量0
     */
    private BigDecimal used = BigDecimal.ZERO;

    /**
     * 施工项的限量，-1表示不限量
     */
    private BigDecimal limit = BigDecimal.valueOf(-1);

    /**
     * 施工项类型：空间带出(room)、主材带出(material)
     */
    private String ref;
    /**
     * 超量的价格
     */
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /**
     * 1代表默认带出，0代表不是默认带出
     */
    private Integer bindByDefault;
}
