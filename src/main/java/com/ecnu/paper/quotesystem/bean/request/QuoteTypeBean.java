package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class QuoteTypeBean {
    @ApiModelProperty(value = "装饰公司")
    private String decorationCompany;
    @ApiModelProperty(value = "装修类型id")
    private Long decorationTypeId;
    @ApiModelProperty(value = "报价类型名称")
    private String name;
    @ApiModelProperty(value = "报价类型编码")
    private String code;
    @ApiModelProperty(value = "报价类型描述")
    private String description;
}
