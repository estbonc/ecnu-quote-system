package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;

@Data
public class PackageBagRequestBean {

    /**
     * 关联套餐版本ID
     */
    @QueryParam("packageVersionId")
    @ApiParam(value = "套餐版本ID", required = true, example = "300990")
    private Long packageVersionId;

    /**
     * 关联套餐版本ID
     */
    @QueryParam("bagId")
    @ApiParam(value = "套餐礼包ID", required = true, example = "300990")
    private Long bagId;

    /**
     * 大礼包名称
     */
    @QueryParam("bagName")
    @ApiParam(value = "大礼包名称", required = true, example = "装饰设计包")
    private String bagName;

    /**
     * 编码
     */
    @QueryParam("bagCode")
    @ApiParam(value = "大礼包编码", required = true, example = "ZSSJB")
    private String bagCode;

    /**
     * 描述信息
     */
    @QueryParam("bagDesc")
    @ApiParam(value = "描述信息", required = true, example = "描述信息")
    private String bagDesc;

    /**
     * 大礼包总价
     */
    @QueryParam("amount")
    @ApiParam(value = "总价", required = true, example = "2000")
    private BigDecimal amount;


    @Override
    public String toString() {
        return "PackageBagRequestBean{" +
                ", packageVersionId=" + packageVersionId +
                ", bagName='" + bagName + '\'' +
                ", bagCode='" + bagCode + '\'' +
                ", bagDesc='" + bagDesc + '\'' +
                ", amount=" + amount +
                '}';
    }
}
