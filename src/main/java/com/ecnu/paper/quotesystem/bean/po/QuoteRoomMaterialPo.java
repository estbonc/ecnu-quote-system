package com.ecnu.paper.quotesystem.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.juran.quote.bean.enums.QuoteItemTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Data
@Document(collection = "quoteRoomMaterial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteRoomMaterialPo implements Serializable {

    /**
     * 空间报价主材ID
     */
    private Long quoteRoomMaterialId;
    /**
     * 空间报价ID
     */
    private Long quoteRoomId;
    /**
     * 主材类型
     *
     * @see com.juran.quote.bean.enums.QuoteItemTypeEnum
     */
    private String quoteItemType = QuoteItemTypeEnum.INNER_MM.getType();

    /**
     * 主材ID
     */
    private Long materialId;

    /**
     * 主材名称
     */
    private String materialName;

    /**
     * 主材编码
     */
    private String materialCode;

    /**
     * 主材单位名称
     */
    private String materialUnitName;

    /**
     * 主材单位编码
     */
    private String materialUnitCode;

    /**
     * 主材限制数量
     */
    private BigDecimal limitQuantity;

    /**
     * 主材使用的数量,默认为0
     */
    private BigDecimal usedQuantity = BigDecimal.ZERO;

    /**
     * 带出的施工项
     */
    private List<QuoteRoomConstructPo> constructList = Collections.emptyList();

    /**
     * 该主材的品牌和sku，只是显示用
     */
    private List<QuoteRoomMaterialBrandPo> brandList = Lists.newArrayList();

    /**
     * 空间名称 第四部显示用
     */
    @Transient
    private String quoteRoomName = "";

    /**
     * 选中的品牌商品信息
     */
    @Transient
    private String selectBrandCode;
    @Transient
    private String selectProductCode;
    @Transient
    private String selectProductSpec;
    @Transient
    private String selectBrandName;
    @Transient
    private String selectProductName;
    @Transient
    private String selectProductModel;

    /**
     * 超量的价格
     */
    private BigDecimal unitPrice = BigDecimal.ZERO;
}
