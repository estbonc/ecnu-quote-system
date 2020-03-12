package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/2 15:03
 * @description
 */
@Data
@Document(collection = "chargeType")
public class ChargeTypePo extends BasePo {

    /**
     * id
     */
    private Long chargeTypeId;

    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 费用类型
     */
    private String chargeType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     *  计算方式
     *  @see com.juran.quote.bean.enums.ChargeCalculationTypeEnums
     */
    private Integer calculationType;

    /**
     * 固定金额数量
     */
    private BigDecimal amount;

    /**
     *  计算比例
     */
    private BigDecimal rate;

    /**
     * 比例基数项
     * @see com.juran.quote.bean.enums.ChargeRateBaseEnums
     */
    private Integer rateBase;
}
