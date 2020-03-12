package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/10 15:58
 * @description
 */
@Data
public class BatchUploadConstructPriceRequest {

    @ApiModelProperty(value = "批次号", required = true)
    private String batchNum;

    @ApiModelProperty("id")
    private Long batchResultId;

    @ApiModelProperty(value = "装饰公司", required = true)
    private String decorationCompany;

    @ApiModelProperty(value = "装修类型id", required = true)
    private Long decorationTypeId;

    @ApiModelProperty(value = "装修类型", required = true)
    private String decorationType;

    @ApiModelProperty(value = "报价版本id", required = true)
    private Long quoteVersionId;

    @ApiModelProperty(value = "报价版本", required = true)
    private String quoteVersion;

    @ApiModelProperty(value = "报价类型id", required = true)
    private Long quoteTypeId;

    @ApiModelProperty(value = "报价类型", required = true)
    private String quoteType;

    @ApiModelProperty(value = "施工项编码", required = true)
    private String constructCode;

    @ApiModelProperty("客户报价")
    private BigDecimal customerPrice;

    @ApiModelProperty("工长报价")
    private BigDecimal foremanPrice;

    @ApiModelProperty("有效状态")
    private Integer status;

    @ApiModelProperty("单位")
    private String unitCode;

    @ApiModelProperty("户型id")
    private Long houseTypeId;

    @ApiModelProperty("户型")
    private String houseType;

    @ApiModelProperty("是否限量")
    private Boolean limit;

    @ApiModelProperty("限量值")
    private Integer limitAmount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("操作者")
    private String updateBy;
}
