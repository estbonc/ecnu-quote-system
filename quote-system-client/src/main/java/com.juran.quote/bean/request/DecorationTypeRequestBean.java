package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DecorationTypeRequestBean{
    @ApiModelProperty(notes = "装修类型名称")
    private String name;
    @ApiModelProperty(notes = "装修类型描述")
    private String description;
    @ApiModelProperty(notes = "装修公司")
    private String decorationCompany;
}
