package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PackageVersionResponseBean implements Serializable {
    @ApiModelProperty(value = "套餐版本ID")
    private Long packageVersionId;
    @ApiModelProperty(value = "套餐名称")
    private String packageName;
    @ApiModelProperty(value = "套餐版本编码")
    private String version;
    @ApiModelProperty(value = "套餐版本起始时间")
    private Long startTime;
    @ApiModelProperty(value = "套餐版本失效时间")
    private Long endTime;
    @ApiModelProperty(value = "适用区域")
    private String region;
    @ApiModelProperty(value = "适用门店")
    private List<StoreRespBean> stores;
}
