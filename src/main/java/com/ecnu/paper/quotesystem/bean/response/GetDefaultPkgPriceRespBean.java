package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetDefaultPkgPriceRespBean implements Serializable {
    private static final long serialVersionUID = 7347939796060366064L;
    @ApiModelProperty("房型")
    private String houseType;
    @ApiModelProperty("套内面积")
    private BigDecimal innerArea;
    @ApiModelProperty("基础价格")
    private BigDecimal basePrice;
}
