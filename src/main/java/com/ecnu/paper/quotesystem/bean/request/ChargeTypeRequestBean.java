package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/2 15:10
 * @description
 */
@Data
public class ChargeTypeRequestBean {

    @ApiModelProperty("装饰公司")
    private String decorationCompany;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("计算方式")
    private Integer calculationType;

    @ApiModelProperty("固定金额数量")
    private BigDecimal amount;

    @ApiModelProperty("计算比例")
    private BigDecimal rate;

    @ApiModelProperty("比例基数项")
    private Integer rateBase;
}
