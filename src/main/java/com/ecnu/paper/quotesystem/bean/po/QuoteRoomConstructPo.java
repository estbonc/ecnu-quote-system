package com.ecnu.paper.quotesystem.bean.po;

import com.juran.quote.bean.enums.QuoteItemTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;


@Data
public class QuoteRoomConstructPo extends SelectedConstruct {

    /**
     * 施工项类型
     *
     * @see com.juran.quote.bean.enums.QuoteItemTypeEnum
     */
    private String quoteItemType = QuoteItemTypeEnum.INNER_CI.getType();

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
    @Transient
    private String constructCode;

    /**
     * 施工项名称
     */
    @Transient
    private String constructName;

    /**
     * 单位
     */
    @Transient
    private String unit;

    /**
     * 单位编码
     */
    @Transient
    private String unitCode;

    /**
     * 描述信息
     */
    @Transient
    private String description;

    /**
     * 备注
     */
    @Transient
    private String remark;

    @Transient
    private String bindMaterialCode;
}
