package com.ecnu.paper.quotesystem.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.juran.quote.bean.enums.QuoteItemTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GetQuoteMaterialDetailRespBean implements Serializable {
    private static final long serialVersionUID = -3976544883470900223L;
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
    private String quoteItemType= QuoteItemTypeEnum.INNER_MM.getType();

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
    private List<Long> selectedConstruct = Lists.newArrayList();

    /**
     * 该主材的品牌和sku，只是显示用
     */
    private List<QuoteRoomMaterialBrandRespBean> brandList= Lists.newArrayList();
    /**
     * 选中的品牌ID
     */
    private String selectBrandCode;

    private String selectProductCode;

    private String selectProductSpec;
    private String selectBrandName;
    private String selectProductName;
    private String quoteRoomName="";
    private String selectProductModel;

    /**
     * 带出的施工项
     */
    private List<GetQuoteCIDetailRespBean> constructList= Collections.EMPTY_LIST;
    /**
     * 超量的价格
     */
    private BigDecimal unitPrice=BigDecimal.ZERO;
}
