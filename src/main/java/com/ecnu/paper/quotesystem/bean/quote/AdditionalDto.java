package com.ecnu.paper.quotesystem.bean.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 3D-bomList-item中的额外参数
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdditionalDto implements Serializable {
    private static final long serialVersionUID = -2431894494185787880L;
    /**
     * 位置
     */
    private String position;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 瓷砖数
     */
    private BigDecimal tileNumber;
    /**
     * 瓷砖长
     */
    private BigDecimal xTileSize;
    /**
     * 瓷砖宽
     */
    private BigDecimal yTileSize;
}
