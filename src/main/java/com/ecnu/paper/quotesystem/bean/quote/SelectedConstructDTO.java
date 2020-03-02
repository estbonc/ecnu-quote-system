package com.ecnu.paper.quotesystem.bean.quote;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by dell on 2017/12/7.
 */
@Data
public class SelectedConstructDTO implements Serializable {

    private static final long serialVersionUID = 6162086468713891933L;

    /**
     * 施工项ID
     */
    @QueryParam("cid")
    @ApiParam(value = "施工项ID", required = true, example = "900001")
    private Long cid;

    /**
     * 已使用的数量,默认数量0
     */
    @QueryParam("used")
    @ApiParam(value = "使用量", required = true, example = "0")
    private BigDecimal used = BigDecimal.ZERO;

    /**
     * 施工项的限量，-1表示不限量
     */
    @QueryParam("limit")
    @ApiParam(value = "限量值", required = true, example = "5")
    private BigDecimal limit;

    /**
     * 施工项类型：空间带出(room)、主材带出(material)
     */
    @QueryParam("ref")
    @ApiParam(value = "带出类型", required = true, example = "room")
    private String ref;

    /**
     * 超量的价格
     */
    @QueryParam("unitPrice")
    @ApiParam(value = "单价", required = true, example = "50")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public SelectedConstructDTO() {
        this.used = BigDecimal.ZERO;
        this.limit = BigDecimal.valueOf(-1);
    }
}
