package com.ecnu.paper.quotesystem.bean.po;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 品牌
 */
@Data
@Document(collection = "brand")
public class Brand {

    private Long uniqueId;

    /**
     * 品牌ID
     */
    private Long brandId;

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
    private List<Product> product= Lists.newArrayList();

}
