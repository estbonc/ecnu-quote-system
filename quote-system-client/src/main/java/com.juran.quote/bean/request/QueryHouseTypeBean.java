package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 30/09/2018 10:22 AM
 * @Description: 条件分页查询户型vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QueryHouseTypeBean extends BasePageBean{

    @ApiParam("装饰公司")
    @QueryParam("decorationCompany")
    private String decorationCompany;
    @ApiParam("创建日期")
    @QueryParam("createTime")
    private Long createTime;
    @ApiParam("状态")
    @QueryParam("status")
    private Integer status;

}
