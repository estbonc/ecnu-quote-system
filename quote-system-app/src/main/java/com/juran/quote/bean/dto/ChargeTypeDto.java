package com.juran.quote.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ChargeTypeDto {

    @ApiModelProperty(value = "额外收费项id", example = "12345678")
    private Long chargeTypeId;

    @ApiModelProperty("装饰公司")
    private String decorationCompany;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("计算方式编码")
    private Integer calculationTypeCode;

    @ApiModelProperty("计算方式")
    private String calculationType;

    @ApiModelProperty("固定金额数量")
    private BigDecimal amount;

    @ApiModelProperty("计算比例")
    private BigDecimal rate;

    @ApiModelProperty("比例基数项编码")
    private Integer rateBaseCode;

    @ApiModelProperty("比例基数项")
    private String rateBase;
}
