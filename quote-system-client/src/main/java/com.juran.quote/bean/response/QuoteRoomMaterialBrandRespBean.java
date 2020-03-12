package com.juran.quote.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuoteRoomMaterialBrandRespBean implements Serializable {
    private static final long serialVersionUID = -6986017886906515281L;

    private Long quoteRoomMaterialBrandId;
    /**
     * 空间报价主材ID
     */
    private Long quoteRoomMaterialId;
    /**
     * 品牌ID
     */
    private Long brandId;
    /**
     * 空间报价ID
     */
    private long quoteRoomId;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 选中状态
     */
    private Boolean selected = Boolean.FALSE;

    /**
     * 品牌下的商品列表
     */
    private List<ProductRespBean> product;
}
