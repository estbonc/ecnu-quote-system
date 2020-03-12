package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PackageConstructRequestBean implements Serializable {

    private static final long serialVersionUID = 6676098618519327458L;

    @QueryParam("packageVersionId")
    @ApiParam(value = "套餐版本ID", required = true, example = "500001")
    private Long packageVersionId;

    @QueryParam("erpVersion")
    @ApiParam(value = "版本编号", required = true, example = "LWBJ-20171001")
    private String erpVersion;

    @QueryParam("houseType")
    @ApiParam(value = "房屋类型", required = true, example = "L2B2")
    private String houseType;

    @QueryParam("constructId")
    @ApiParam(value = "施工项ID", required = true, example = "900001")
    private Long constructId;

    @QueryParam("limitQuantity")
    @ApiParam(value = "限制数量", required = true, example = "5")
    private BigDecimal limitQuantity;

    @QueryParam("unitPrice")
    @ApiParam(value = "单价", required = true, example = "59")
    private BigDecimal unitPrice;
}
