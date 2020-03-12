package com.juran.quote.bean.dto.excel;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 08/12/2018 5:11 PM
 * @Description: 施工项列表数据Dto
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class ConstructListDto {


    /**
     * 空间：
     */
    private String roomName;


    /**
     * 施工项编码：
     */
    private String constructCode;

    /**
     * 施工项名称：
     */
    private String constructName;

    /**
     * 工艺材料简介：
     */
    private String desc;

    /**
     * 辅助名称规格：
     */
    private String assitSpec;

    /**
     * 单价：
     */
    private String unitPrice;

    /**
     * 数量：
     */
    private String usedQuantity;
    /**
     * 小计：
     */
    private String usePrice;



}
