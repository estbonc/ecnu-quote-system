package com.juran.quote.bean.response;

import com.juran.quote.bean.quote.BomRoomDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class QuoteBaseInfoRespBean implements Serializable {
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
     * 房型id
     */
    private Long houseTypeId;
    /**
     * 房型
     */
    private String houseType;
    /**
     * 房型名称
     */
    private String houseTypeName;

    /**
     * 设计师名称
     */
    private String memberName;

    /**
     * 房间信息
     */
    private List<BomRoomDto> roomBaseInfo;

    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 装修类型
     */
    private String decorationType;

    /**
     * 装修类型id
     */
    private Long decorationTypeId;

    /**
     * 报价类型
     */
    private String quoteType;

    /**
     * 报价类型id
     */
    private Long quoteTypeId;

    /**
     * 是否绑定装修项目，0：未绑定，1：绑定
     */
    private int isBindProject;
}
