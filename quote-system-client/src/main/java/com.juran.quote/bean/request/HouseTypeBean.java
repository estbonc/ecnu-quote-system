package com.juran.quote.bean.request;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 5:11 PM
 * @Description: 房型Vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel
public class HouseTypeBean  extends BaseBean{

    @ApiModelProperty(value = "空间id", required = false, example = "123456")
    private Long houseTypeId;

    @ApiModelProperty(value = "户型名称", required = true, example = "3室1厅1卫")
    private String houseType;

    @ApiModelProperty(value = "户型编码", required = true, example = "L3B2")
    private String houseTypeCode;

    @ApiModelProperty(value = "装饰公司", required = false, example = "设计创客")
    private String decorationCompany;

    @ApiModelProperty(value = "空间列表", required = true)
    private Set<Long> rooms= Sets.newHashSet();

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "状态", required = false, example ="1")
    private Integer status;

}
