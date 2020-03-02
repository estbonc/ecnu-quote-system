package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetQuoteBaseInfoRespBean implements Serializable {
    private static final long serialVersionUID = 533219948580005792L;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 客户手机号
     */
    private String customerMobile;
    /**
     * 省编码
     */
    private String provinceId;
    /**
     * 市编码
     */
    private String cityId;
    /**
     * 区编码
     */
    private String districtId;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 小区名称
     */
    private String communityName;
    /**
     * 套内面积
     */
    private BigDecimal innerArea;
    /**
     * 房型
     */
    private String houseType;
    /**
     * 房型名称
     */
    private String houseTypeName;
    /**
     * 房屋状态
     */
    private String houseState;
    /**
     * 报价级别ID
     */
    private String packageId;
    private int isBindProject;
    /**
     * 设计师名称
     */
    private String memberName;
    /**
     * 门店ID
     */
    private String storeId;
    private List<PackageInfoRespBean> packageInfos;
    private List<StoreRespBean> storeList;
}