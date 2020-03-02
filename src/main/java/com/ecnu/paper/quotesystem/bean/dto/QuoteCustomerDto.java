package com.ecnu.paper.quotesystem.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class QuoteCustomerDto {
    @ApiModelProperty(value = "业主姓名", example = "张三")
    private String customerName;

    @ApiModelProperty(value = "客户手机号", example = "1888888888")
    private String customerMobile;

    @ApiModelProperty(value = "省份编码", example = "100100")
    private String provinceId;

    @ApiModelProperty(value = "城市编码", example = "100100")
    private String cityId;

    @ApiModelProperty(value = "行政区域编码", example = "100100")
    private String districtId;

    @ApiModelProperty(value = "省份", example = "江苏省")
    private String province;

    @ApiModelProperty(value = "城市名称", example = "南京市")
    private String city;

    @ApiModelProperty(value = "行政区域", example = "鼓楼区")
    private String district;

    @ApiModelProperty(value = "小区名", example = "东方明珠一期")
    private String communityName;

    public String getAddress(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(province).append(city).append(district).append(communityName);
        return stringBuilder.toString();
    }
}
