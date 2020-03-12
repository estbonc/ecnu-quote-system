package com.juran.quote.bean.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PackageVersionRequestBean  implements Serializable {

    @QueryParam("packageVersionId")
    @ApiParam(value = "套餐版本ID", required = false, example = "1")
    private Long packageVersionId;

    @QueryParam("packageId")
    @ApiParam(value = "套餐ID", required = true, example = "1")
    private Long packageId;

    @QueryParam("region")
    @ApiParam(value = "适用区域", required = true, example = "110100")
    private String region;

    @QueryParam("version")
    @ApiParam(value = "套餐版本", required = true, example = "201712")
    private String version;

    @QueryParam("startTime")
    @ApiParam(value = "有效开始时间", required = true)
    private Long startTime;

    @QueryParam("endTime")
    @ApiParam(value = "有效结束时间", required = true)
    private Long endTime;

    @QueryParam("stores")
    @ApiParam(value = "适用门店", required = true, example = "[LWK8,LW57]")
    private List<String> stores;

    @Override
    public String toString() {
        return "PackageVersionRequestBean{" +
                "packageVersionId=" + packageVersionId +
                ", packageId=" + packageId +
                ", region='" + region + '\'' +
                ", version='" + version + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stores=" + stores +
                '}';
    }
}
