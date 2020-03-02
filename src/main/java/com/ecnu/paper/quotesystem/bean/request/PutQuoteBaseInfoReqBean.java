package com.ecnu.paper.quotesystem.bean.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
public class PutQuoteBaseInfoReqBean implements Serializable {

    private static final long serialVersionUID = -515144764140544415L;

    @ApiModelProperty(value = "报价ID", example = "123456789")
    private Long quoteId;

    @ApiModelProperty(value = "户型", example = "2室1卫")
    private String houseType;

    @ApiModelProperty(value = "户型id", example = "12345678")
    private Long houseTypeId;

    @ApiModelProperty(value = "装饰公司", example = "设计创客")
    private String decorationCompany;

    @ApiModelProperty(value = "装修类型", example = "个性化")
    private String decorationType;

    @ApiModelProperty(value = "装修类型id", example = "12345678")
    private Long decorationTypeId;

    @ApiModelProperty(value = "报价类型", example = "个性化方案1")
    private String quoteType;

    @ApiModelProperty(value = "报价类型id", example = "12345678")
    private Long quoteTypeId;

    @ApiModelProperty(value = "套内面积", example = "100")
    private BigDecimal innerArea;

    @ApiModelProperty(value = "设计方案id", example = "fc80f1ef-4937-41a4-9443-7ebf95500143")
    private String caseId;

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

    @ApiModelProperty(value = "设计师姓名", example = "张嘎子")
    private String designerName;

    @ApiModelProperty(value = "是否绑定装修项目，0：未绑定，1：绑定", example = "0")
    private int isBindProject;
}
