package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 施工项
 */
@Data
@Document(collection = "construct_bak")
public class ConstructBackup {

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
    /**
     * 删除时间
     */
    private Date removeTime;

}
