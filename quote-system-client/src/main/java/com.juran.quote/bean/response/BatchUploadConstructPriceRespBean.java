package com.juran.quote.bean.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/9 15:03
 * @description
 */
@Data
public class BatchUploadConstructPriceRespBean {
    /**
     * 批次号
     */
    private String batchNum;

    private Long batchResultId;

    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 装修类型id
     */
    private Long decorationTypeId;

    /**
     * 装修类型
     */
    private String decorationType;

    /**
     * 报价版本id
     */
    private Long quoteVersionId;

    /**
     * 报价版本
     */
    private String quoteVersion;

    /**
     * 报价类型id
     */
    private Long quoteTypeId;

    /**
     * 报价类型id
     */
    private String quoteType;

    /**
     * 施工项编码
     */
    private String constructCode;


    /**
     * 客户报价
     */
    private BigDecimal customerPrice;

    /**
     * 工长报价
     */
    private BigDecimal foremanPrice;

    /**
     * 有效状态
     */
    private Integer status;

    /**
     * 单位
     */
    private String unitCode;

    /**
     * 户型id
     */
    private Long houseTypeId;

    /**
     * 户型
     */
    private String houseType;
    /**
     * 是否限量
     */
    private Boolean limit;

    /**
     * 限量值
     */
    private Integer limitAmount;

    private Date createTime;

    private Date updateTime;

    /**
     * 操作者
     */
    private String updateBy;

    private Integer result;

    private String message;

    private Integer logStatus;

}
