package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: xiongtao
 * @Date: 07/11/2018 3:29 PM
 * @Description: 计算施工现场带出数量的返回vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel
public class ConstCalcResponse {

    @ApiModelProperty(value = "施工项id", required = true, example = "235711")
    private Long constructId;

    @ApiModelProperty(value = "使用数量变化量", required = true, example = "235711")
    private BigDecimal changeUsed;

}
