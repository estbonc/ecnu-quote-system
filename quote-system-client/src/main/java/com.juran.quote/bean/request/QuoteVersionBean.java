package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 09/10/2018 1:16 PM
 * @Description: 报价版本vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel
public class QuoteVersionBean extends BaseBean {

    @ApiModelProperty(value = "报价版本id", required = false)
    private Long quoteVersionId;

    @ApiModelProperty(value = "装饰公司", required = true)
    private String decorationCompany;

    @ApiModelProperty(value = "装修类型id", required = true)
    private Long decorationTypeId;

    @ApiModelProperty(value = "报价类型id", required = true)
    private Long quoteTypeId;

    @ApiModelProperty(value = "报价版本编码", required = true)
    private String quoteVersionCode;

    @ApiModelProperty(value = "生效日期", required = true)
    private Date startTime;

    @ApiModelProperty(value = "失效日期", required = true)
    private Date endTime;

    @ApiModelProperty(value = "区域代码", required = true)
    private List<String> region;

    @ApiModelProperty(value = "门店列表", required = false)
    private List<String> stores;

    @ApiModelProperty(value = "报价版本状态", required = false)
    private Integer status;


}
