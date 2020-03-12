package com.juran.quote.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/2 15:10
 * @description
 */
@Data
public class ChargeTypeResponseBean {

    private Long chargeTypeId;

    @ApiModelProperty("装饰公司")
    private String decorationCompany;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("状态 0删除  1有效")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建日期")
    private Date createTime;

    @ApiModelProperty("计算方式编码")
    private Integer calculationTypeCode;

    @ApiModelProperty("计算方式")
    private String calculationType;

    @ApiModelProperty("固定金额数量")
    private BigDecimal amount;

    @ApiModelProperty("计算比例")
    private BigDecimal rate;

    @ApiModelProperty("比例基数项编码")
    private Integer rateBaseCode;

    @ApiModelProperty("比例基数项")
    private String rateBase;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
