package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;

/**
 * @Author: xiongtao
 * @Date: 28/09/2018 10:25 AM
 * @Description: 导出excel条件
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QueryExportConstructbean {

    @ApiParam("施工项类别")
    @QueryParam("constructCategory")
    String constructCategory;
    @ApiParam("施工项分类")
    @QueryParam("constructItem")
    String constructItem;
    @ApiParam("施工项编码")
    @QueryParam("constructCode")
    String constructCode;
    @ApiParam("施工项名称")
    @QueryParam("constructName")
    String constructName;
    @ApiParam("施工项状态")
    @QueryParam("status")
    Integer status;
    @ApiParam("装饰公司")
    @QueryParam("decorationCompany")
    String decorationCompany;


}
