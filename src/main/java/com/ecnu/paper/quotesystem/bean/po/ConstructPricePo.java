package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/10/8 17:21
 * @description
 */
@Data
@Document(collection = "constructPrice")
public class ConstructPricePo extends BasePo implements Serializable {

    private static final long serialVersionUID = 1780487688220162398L;

    /**
     * 施工项价格Id
     */
    @Indexed(unique = true)
    private Long constructPriceId;

    /**
     * 批量上传编码
     */
    private String batchNum;
    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 装修类型
     */
    private Long decorationTypeId;

    /**
     * 报价版本
     */
    private Long quoteVersionId;

    /**
     * 报价类型
     */
    private Long quoteTypeId;

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
     * 户型
     */
    private Long houseTypeId;

    /**
     * 是否限量
     */
    private Boolean limit;

    /**
     * 限量值
     */
    private Integer limitAmount;

}
