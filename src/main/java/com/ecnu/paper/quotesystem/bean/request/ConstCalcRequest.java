package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: xiongtao
 * @Date: 07/11/2018 1:47 PM
 * @Description: 计算主材带出施工项数量的请vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel
public class ConstCalcRequest {



    @ApiModelProperty(value = "报价id", required = true, example = "235711")
    private Long quoteId;

    @ApiModelProperty(value = "施工编码", required = true, example = "235711")
    private String categoryId;

    @ApiModelProperty(value = "使用数量-旧", required = true, example = "10")
    private BigDecimal oldUsed;

    @ApiModelProperty(value = "使用数量-新", required = true, example = "9")
    private BigDecimal newUsed;

    @ApiModelProperty(value = "房间类型", required = true, example = "Bathroom")
    private String roomType;

    @ApiModelProperty(value = "房间名称", required = true, example = "卫生间")
    private String roomName;


}
