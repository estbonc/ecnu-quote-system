package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/22 13:50
 * @description
 */
@Data
public class CalculationFormulaBean {

    @ApiModelProperty("id")
    private Long expressionId;

    @ApiModelProperty("施工项id")
    private Long constructId;

    @ApiModelProperty("计算公式")
    private String expression;

}
