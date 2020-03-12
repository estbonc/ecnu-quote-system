package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;


@Data
@Document(collection = "quoteRoomMaterialBrand")
public class QuoteRoomMaterialBrandPo implements Serializable {
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
    private List<Product> product;
}
