package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: xiongtao
 * @Date: 08/11/2018 2:38 PM
 * @Description: 分页查询支持施工项返回bean
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QuerySupportConstructRespBean {

    @ApiModelProperty(value = "施工项id")
    private Long constructId;

    @ApiModelProperty(value = "施工项编码")
    private String constructCode;

    @ApiModelProperty(value = "施工项名称")
    private String constructName;

    @ApiModelProperty(value = "客户报价")
    private BigDecimal customerPrice;

    @ApiModelProperty(value = "单位")
    private String unitCode;

    @ApiModelProperty(value = "单位名称")
    private String unitCodeName;

    @ApiModelProperty(value = "工艺材料简介")
    private String desc;

    @ApiModelProperty(value = "辅料名称规格")
    private String assitSpec;
}