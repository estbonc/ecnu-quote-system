package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 3:00 PM
 * @Description: 基础字段
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class BaseBean {


    @ApiModelProperty(value = "创建日期", required = false)
    private Date createTime;

    @ApiModelProperty(value = "更新日期", required = false)
    private Date updateTime;

    @ApiModelProperty(value = "创建人/更新人", required = false, example = "bigapple")
    private String updateBy;

}
