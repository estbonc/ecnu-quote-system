package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 施工项
 */
@Data
@Document(collection = "construct")
public class Construct implements Serializable {

    private static final long serialVersionUID = 1780487688220162398L;
    @Id
    private String id;
    /**
     * 施工项ID
     */
    @Indexed(unique = true)
    private Long constructId;

    /**
     * 施工项编码
     */
    private String constructCode;

    /**
     * 施工项名称
     */
    private String constructName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单位编码
     */
    private String unitCode;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 备注
     */
    private String remark;

    /**
     * 有效状态
     */
    private Integer status;

    /**
     * 客户报价
     */
    private BigDecimal customerPrice;

    /**
     * 工长报价
     */
    private BigDecimal foremanPrice;

    /**
     * 报价版本
     */
    private String packageVersion;

}
