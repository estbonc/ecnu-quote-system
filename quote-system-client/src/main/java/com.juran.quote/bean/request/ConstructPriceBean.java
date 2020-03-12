package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/8 18:26
 * @description
 */
@Data
public class ConstructPriceBean {

    @ApiModelProperty(value = "批量上传编码")
    private String batchNum;

    @ApiModelProperty(value = "施工项价格Id")
    private Long constructPriceId;

    @ApiModelProperty(value = "装饰公司")
    private String decorationCompany;

    @ApiModelProperty(value = "装修类型id")
    private Long decorationTypeId;

    @ApiModelProperty(value = "装修类型")
    private String decorationType;

    @ApiModelProperty(value = "报价版本id")
    private Long quoteVersionId;

    @ApiModelProperty(value = "报价版本")
    private String quoteVersion;

    @ApiModelProperty(value = "报价类型")
    private Long quoteTypeId;

    @ApiModelProperty(value = "报价类型id")
    private String quoteType;

    @ApiModelProperty(value = "施工项编码")
    private String constructCode;

    @ApiModelProperty(value = "客户报价")
    private BigDecimal customerPrice;

    @ApiModelProperty(value = "工长报价")
    private BigDecimal foremanPrice;

    @ApiModelProperty(value = "有效状态")
    private Integer status;

    @ApiModelProperty(value = "单位")
    private String unitCode;

    @ApiModelProperty(value = "户型id")
    private Long houseTypeId;

    @ApiModelProperty(value = "户型")
    private String houseType;

    @ApiModelProperty(value = "是否限量")
    private Boolean limit;

    @ApiModelProperty(value = "限量值")
    private Integer limitAmount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "操作者")
    private String updateBy;

}
