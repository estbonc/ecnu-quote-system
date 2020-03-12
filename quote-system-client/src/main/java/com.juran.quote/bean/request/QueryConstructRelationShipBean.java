package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 23/10/2018 4:20 PM
 * @Description: 查询施工项带出vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QueryConstructRelationShipBean {


    @ApiParam("施工项id")
    @QueryParam("constructId")
    private Long constructId;

    @ApiParam("报价类型id")
    @QueryParam("quoteTypeId")
    private Long quoteTypeId;

    @ApiParam("户型id")
    @QueryParam("houseTypeId")
    private Long houseTypeId;

    @ApiParam("装饰公司")
    @QueryParam("decorationCompany")
    private String  decorationCompany;





}
