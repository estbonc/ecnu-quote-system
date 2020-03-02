package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BatchUploadConstructRequest {

    @ApiModelProperty(value = "施工项id", required = true, example = "900001")
    private String batchNum;

    @ApiModelProperty(value = "施工项id", required = false, example = "900001")
    private Long constructId;

    @ApiModelProperty(value = "施工项类别", required = true, example = "标准项目")
    private String constructCategory;

    @ApiModelProperty(value = "施工项分类", required = true, example = "安装")
    private String constructItem;

    @ApiModelProperty(value = "施工项编码", required = true, example = "TC01")
    private String constructCode;

    @ApiModelProperty(value = "施工项名称", required = true, example = "水电09")
    private String constructName;

    @ApiModelProperty(value = "计量单位", required = true, example = "米")
    private String unitCode;

    @ApiModelProperty(value = "工艺材料简介", required = false, example = "工艺材料简介")
    private String desc;

    @ApiModelProperty(value = "辅料名称规格", required = false, example = "辅料名称规格")
    private String assitSpec;

    @ApiModelProperty(value = "验收标准", required = false, example = "验收")
    private String standard;

    @ApiModelProperty(value = "标准出处", required = false, example = "出处")
    private String sourceOfStandard;

    @ApiModelProperty(value = "施工项状态", required = false, example = "0")
    private Integer status;

    @ApiModelProperty(value = "装饰公司", required = false, example = "设计创客")
    private String decorationCompany;

    @ApiModelProperty(value = "备注", required = false, example = "xxxxxx")
    private String remark;

    @ApiModelProperty(value = "创建时间", required = false, example = "900001")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", required = false, example = "900001")
    private Date updateTime;
    @ApiModelProperty(value = "更新人", required = true, example = "小明")
    private String updateBy;
}
