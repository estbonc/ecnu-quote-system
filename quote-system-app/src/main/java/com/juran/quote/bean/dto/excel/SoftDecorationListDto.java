package com.juran.quote.bean.dto.excel;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 08/12/2018 1:46 PM
 * @Description: 软装列表dto
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class SoftDecorationListDto {

    /**
     * 空间
     */
    private String roomName;

    /**
     *商品名称
     */
    private String productName;
    /**
     *品牌
     */
    private String brandName;
    /**
     *单价
     */
    private String unitPrice;
    /**
     *数量
     */
    private String useQuantity;
    /**
     *小计
     */
    private String usePrice;
}
