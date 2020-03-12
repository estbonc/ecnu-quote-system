package com.juran.quote.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PackageConstructResponseBean implements Serializable {
    @ApiModelProperty(value = "套餐施工项id")
    private Long packageConstructId;
    @ApiModelProperty(value = "施工项id")
    private Long constructId;
    @ApiModelProperty(value = "施工项名称")
    private String constructName;
    @ApiModelProperty(value = "施工项编号")
    private String constructCode;
    @ApiModelProperty(value = "套餐版本编号")
    private String packageVersion;
    @ApiModelProperty(value = "户型")
    private String houseType;
    @ApiModelProperty(value = "客户报价")
    private BigDecimal customerPrice;
    @ApiModelProperty(value = "施工队报价")
    private BigDecimal constructionPrice;
    @ApiModelProperty(value = "单位")
    private String unitCode;
    @ApiModelProperty(value = "是否限量")
    private boolean hasLimitation;
    @ApiModelProperty(value = "限量值")
    private BigDecimal limit;
}
