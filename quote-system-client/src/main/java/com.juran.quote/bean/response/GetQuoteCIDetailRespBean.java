package com.juran.quote.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.juran.quote.bean.enums.QuoteItemTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GetQuoteCIDetailRespBean implements Serializable {
    private static final long serialVersionUID = -1433331370061844274L;
    /**
     * 施工项类型
     *
     * @see com.juran.quote.bean.enums.QuoteItemTypeEnum
     */
    private String quoteItemType=QuoteItemTypeEnum.INNER_CI.getType();

    /**
     * 空间ID
     */
    private Long quoteRoomId;

    /**
     * 大礼包编号
     */
    private Long bagId;

    /**
     * 客户报价
     */
    private BigDecimal customerPrice;

    /**
     * 工长报价
     */
    private BigDecimal foremanPrice;


    /**
     * 施工项编码
     */
    private String constructCode;

    /**
     * 施工项名称
     */
    private String constructName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单位编码
     */
    private String unitCode;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 备注
     */
    private String remark;


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
    private BigDecimal limit = new BigDecimal(-1);

    /**
     * 施工项类型：空间带出(room)、主材带出(material)
     */
    private String ref;
    /**
     * 超量的价格
     */
    private BigDecimal unitPrice=BigDecimal.ZERO;
}
