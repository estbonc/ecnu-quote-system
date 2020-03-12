package com.juran.quote.bean.dto.excel;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 08/12/2018 5:33 PM
 * @Description: 硬装材料
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class HardMaterialListDto {

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
     *型号
     */
    private String model;

    /**
     *规格
     */
    private String spec;
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
