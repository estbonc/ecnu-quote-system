package com.ecnu.paper.quotesystem.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 清单报价-主材实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class QuoteItemRespBean implements Serializable,Cloneable {
    private static final long serialVersionUID = -7760376023290418453L;

    private String itemKey;
    private Long quoteKey;
    private String productSetId;
    private String partNumber;
    private String spec;
    private String catentryName;
    private String catentryType;
    private String catentryId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 空间ID
     */
    private String roomId;
    /**
     * 房型
     */
    private String roomType;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 单位ID
     */
    private String qtyUnitId;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 货币类型
     */
    private String currency;
    /**
     * 数量调整
     */
    private BigDecimal quantityAdjustment;
    /**
     * 总价格
     */
    private BigDecimal totalPrice;
    /**
     * 总调价
     */
    private BigDecimal totalAdjust;
    /**
     * 最后更新时间
     */
    private Date lastUpdate;
    /**
     * @see com.juran.application.quote.bean.enums.QuoteItemTypeEnum
     */
    private String itemType;

    /**
     * 施工项描述信息:工艺做法
     */
    private String decorateMethod;
    /**
     * 施工项描述信息:辅料规格
     */
    private String decorateSpec;
    /**
     * 单位信息
     */
    private String qtyUnitName;
    /**
     * 主材名称
     */
    private String productSetName;
    /**
     * 空间名称
     */
    private String roomName;
}
