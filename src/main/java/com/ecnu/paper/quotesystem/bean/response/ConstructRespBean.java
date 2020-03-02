package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by dell on 2018/1/25.
 */
@Data
public class ConstructRespBean {

    /**
     * 施工项ID
     */
    private Long constructId;

    /**
     * 套餐施工项id
     */
    private Long packageConstructId;

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
     * 有效状态
     */
    private Integer status;

    /**
     * 客户报价
     */
    private BigDecimal customerPrice;

    /**
     * 工长报价
     */
    private BigDecimal foremanPrice;

    /**
     * 限量
     */
    private BigDecimal limit;

}
