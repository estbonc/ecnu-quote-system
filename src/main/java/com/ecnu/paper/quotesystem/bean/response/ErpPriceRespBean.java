package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ErpPriceRespBean implements Serializable {
    private static final long serialVersionUID = -6233010985482423732L;

    @ApiModelProperty(value = "合同金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "工程直接费")
    private BigDecimal projectCost;
    @ApiModelProperty(value = "超量加价")
    private BigDecimal surplusAmount;
    @ApiModelProperty(value = "超市产品金额")
    private BigDecimal marketAmount;
}
