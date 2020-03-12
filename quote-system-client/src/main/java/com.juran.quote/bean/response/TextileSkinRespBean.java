package com.juran.quote.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextileSkinRespBean implements Serializable {
    /**
     * 空间报价家具ID
     */
    private Long quoteRoomTextileId;
    /**
     * 空间报价ID
     */
    private Long quoteRoomId;
    /**
     * 家纺居然sku
     */
    private String productSku;
    /**
     * 家纺淘宝SKU
     */
    private String tbSkuId;
    /**
     * 家纺阿里ID
     */
    private String tbId;
    /**
     * 家纺名称
     */
    private String textileName;
    /**
     * 家纺图片地址
     */
    private String textileImage;
    /**
     * 家纺数量
     */
    private BigDecimal textileNum;
    /**
     * 家纺单价
     */
    private BigDecimal textilePrice;
}
