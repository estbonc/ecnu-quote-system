package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 30/09/2018 10:14 AM
 * @Description: 基础分页请求vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class BasePageBean {


    @ApiParam("第几页 默认：1")
    @DefaultValue("1")
    @QueryParam("offset")
    Integer offset=1;


    @ApiParam("每页大小默认：15")
    @DefaultValue("15")
    @QueryParam("limit")
    Integer limit=15;


}
