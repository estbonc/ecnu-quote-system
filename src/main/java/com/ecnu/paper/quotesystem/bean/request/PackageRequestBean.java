package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;

@Data
public class PackageRequestBean implements Serializable {

    @QueryParam("packageId")
    @ApiModelProperty(required = false,example = "999999", notes = "套餐ID")
    private Long packageId;

    @QueryParam("packageName")
    @ApiModelProperty(notes = "套餐名称", required = true, example = "北舒")
    private String packageName;

    @QueryParam("packageCode")
    @ApiModelProperty(notes = "套餐编码", required = true, example = "BS-OLD")
    private String packageCode;

    @QueryParam("description")
    @ApiModelProperty(notes = "描述信息", required = true, example = "描述信息")
    private String description;

    @QueryParam("status")
    @ApiModelProperty(notes = "套餐状态", required = false, example = "1")
    private Integer status;

    @Override
    public String toString() {
        return "PackageRequestBean{" +
                "packageId=" + packageId +
                ", packageName='" + packageName + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
