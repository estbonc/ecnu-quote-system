package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BagConstructRequestBean implements Serializable {

    @QueryParam("constructId")
    @ApiParam(value = "施工项ID", required = true, example = "1")
    private Long constructId;

    @QueryParam("bagId")
    @ApiParam(value = "大礼包ID", required = true, example = "1")
    private Long bagId;

    @QueryParam("unitPrice")
    @ApiParam(value = "单价", required = true, example = "99")
    private BigDecimal unitPrice;
}
