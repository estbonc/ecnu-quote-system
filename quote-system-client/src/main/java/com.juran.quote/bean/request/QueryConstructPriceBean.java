package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/8 17:53
 * @description
 */
@Data
public class QueryConstructPriceBean extends BasePageBean {

    @ApiParam(value = "批量上传编码")
    @QueryParam("batchNum")
    private String batchNum;

    @ApiParam(value = "装饰公司")
    @QueryParam("decorationCompany")
    private String decorationCompany;

    @ApiParam(value = "装修类型")
    @QueryParam("decorationTypeId")
    private Long decorationTypeId;

    @ApiParam(value = "报价版本")
    @QueryParam("quoteVersionId")
    private Long quoteVersionId;

    @ApiParam(value = "报价类型")
    @QueryParam("quoteTypeId")
    private Long quoteTypeId;

    @ApiParam(value = "施工项编码")
    @QueryParam("constructCode")
    private String constructCode;

}
