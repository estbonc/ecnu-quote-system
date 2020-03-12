package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 07/09/2018 11:06 AM
 * @Description: 列表查询施工项的vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QueryConstructBean extends BasePageBean{


    @ApiParam(value = "批量上传编码")
    @QueryParam("batchNum")
    private String batchNum;

    @ApiParam(value = "施工项类别")
    @QueryParam("constructCategory")
    private String constructCategory;

    @ApiParam(value = "施工项分类")
    @QueryParam("constructItem")
    private String constructItem;

    @ApiParam(value = "施工项编码")
    @QueryParam("constructCode")
    private String constructCode;

    @ApiParam(value = "施工项名称")
    @QueryParam("constructName")
    private String constructName;

    @ApiParam(value = "施工项状态")
    @QueryParam("status")
    private Integer status;

    @ApiParam(value = "装饰公司")
    @QueryParam("decorationCompany")
    private String decorationCompany;




}
