package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "packageVersion")
public class PackageVersion {

    /**
     * 套餐版本ID
     */
    private Long packageVersionId;

    /**
     * 关联套餐ID
     */
    private Long packageId;

    /**
     * 版本信息
     */
    private String version;

    /**
     * 套餐有效开始时间
     */
    private Date startTime;

    /**
     * * 套餐有效结束时间
     */
    private Date endTime;

    /**
     * 套餐使用区域
     */
    private String region;

    /**
     * 生效门店
     */
    private List<String> stores;

    /**
     * * 套餐有效更新时间
     */
    private Date updateTime;

    /**
     * 有效状态
     */
    private Integer status;

    @Override
    public String toString() {
        return "PackageVersion{" +
                "packageVersionId=" + packageVersionId +
                ", packageId=" + packageId +
                ", version='" + version + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", region='" + region + '\'' +
                ", stores=" + stores +
                ", updateTime=" + updateTime +
                ", status=" + status +
                '}';
    }
}
