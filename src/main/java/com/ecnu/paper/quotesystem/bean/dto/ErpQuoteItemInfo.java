package com.ecnu.paper.quotesystem.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ErpQuoteItemInfo implements Serializable {
    private static final long serialVersionUID = -5135663203155341857L;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 施工项code/主材商品编码
     */
    private String itemCode;
    /**
     * 施工项主材商品ID
     */
    private String itemId;
    /**
     * item类型  1-施工项，2-主材，3-其他
     */
    private String itemType;
    /**
     * item描述
     */
    private String itemDesc;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 单位code
     */
    private String unitCode;
    /**
     * 房屋类型code
     */
    private String roomCode;

    /**
     * 房型编码
     */
    private String houseCode;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 限量值
     */
    private BigDecimal limit;

    /**
     * 套餐版本
     */
    private String versionCode;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 超量单价
     */
    private BigDecimal overrunUnit = BigDecimal.ZERO;

    /**
     * 超量金额
     */
    private BigDecimal overrunAmount = BigDecimal.ZERO;

    /**
     * 实收金额
     */
    private BigDecimal realAmount;

    private Long bagId = 0L;

    private String quoteItemType;
}
