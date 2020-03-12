package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 09/10/2018 3:36 PM
 * @Description: 列表查询报价版本的vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QueryQuoteVersionBean extends BasePageBean {


    @ApiParam(value = "装饰公司")
    @QueryParam("decorationCompany")
    private String decorationCompany;

    @ApiParam(value = "装修类型")
    @QueryParam("decorationTypeId")
    private Long decorationTypeId;

    @ApiParam(value = "报价版本")
    @QueryParam("quoteVersionId")
    private Long quoteVersionId;

    @ApiParam(value = "生效日期")
    @QueryParam("startTime")
    private Long startTime;

    @ApiParam(value = "失效日期")
    @QueryParam("endTime")
    private Long endTime;

    @ApiParam(value = "区域")
    @QueryParam("region")
    private String region;

    @ApiParam(value = "门店")
    @QueryParam("store")
    private String store;

    @ApiParam(value = "状态")
    @QueryParam("status")
    private Integer status;


}
