package com.ecnu.paper.quotesystem.bean.dto;

import lombok.Data;

@Data
public class MaterialSpecDto {
    /**
     * 主材单品sku
     */
    private String sku;

    /**
     * 单品规格
     */
    private String spec;

    /**
     * 是否默认带出施工项，1 是，0 否
     */
    private Integer bindByDefault;
}
