package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 施工项
 */
@Data
@Document(collection = "IndividualConstruct")
public class ConstructPo implements Serializable {

    private static final long serialVersionUID = 1780487688220161398L;
    @Id
    private String id;
    /**
     * 施工项ID
     */
    @Indexed(unique = true)
    private Long constructId;

    /**
     * 批量上传编码
     */
    private String batchNum;
    /**
     * 施工项类别
     */
    private String constructCategory;
    /**
     * 施工项分类
     */
    private String constructItem;
    /**
     * 施工项编码
     */
    private String constructCode;
    /**
     * 施工项名称
     */
    private String constructName;
    /**
     * 计量单位
     */
    private String unitCode;
    /**
     * 工艺材料简介
     */
    private String desc;
    /**
     * 辅料名称规格
     */
    private String assitSpec;
    /**
     * 验收标准
     */
    private String standard;
    /**
     * 标准出处
     */
    private String sourceOfStandard;
    /**
     * 施工项状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateBy;
}
