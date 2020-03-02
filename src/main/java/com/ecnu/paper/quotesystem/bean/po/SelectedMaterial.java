package com.ecnu.paper.quotesystem.bean.po;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

/**
 * 主材
 */
@Data
@Document(collection = "material")
public class SelectedMaterial {

    /**
     * 主材ID
     */
    @Indexed(unique = true)
    private Long materialId;
    /**
     * 主材对应的房间
     */
    private Long packageRoomId;

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
    private List<SelectedConstruct> selectedConstruct = Lists.newArrayList();

    /**
     * 主材可选的品牌
     */
    private List<Long> brandList;
    /**
     * 主材可选的品牌
     */
    private List<Brand> brands =Lists.newArrayList();

}
