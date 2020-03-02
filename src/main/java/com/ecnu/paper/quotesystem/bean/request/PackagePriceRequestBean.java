package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;

@Data
public class PackagePriceRequestBean {

    /**
     * 关联套餐版本ID
     */
    @QueryParam("packageVersionId")
    @ApiParam(value = "套餐版本ID", required = true, example = "300990")
    private Long packageVersionId;

    /**
     * 房型
     */
    @QueryParam("houseType")
    @ApiParam(value = "房屋类型", required = true, example = "L2B2")
    private String houseType;

    /**
     * 基本报价
     */
    @QueryParam("baseAmount")
    @ApiParam(value = "套餐基础价格", required = true, example = "89999")
    private BigDecimal baseAmount;

    /**
     * 套餐内面积
     */
    @QueryParam("baseArea")
    @ApiParam(value = "套餐内面积", required = true, example = "85")
    private BigDecimal baseArea;

    /**
     * 超出面积的收费单价
     */
    @QueryParam("unitPrice")
    @ApiParam(value = "超出面积单价", required = true, example = "599")
    private BigDecimal unitPrice;

    @Override
    public String toString() {
        return "PackagePriceRequestBean{" +
                ", packageVersionId=" + packageVersionId +
                ", houseType='" + houseType + '\'' +
                ", baseAmount=" + baseAmount +
                ", baseArea=" + baseArea +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
