package com.ecnu.paper.quotesystem.bean.dto.excel;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 05/12/2018 10:48 AM
 * @Description: 报价sheet1的基本信息基础dto
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class QuoteBaseDto {


    /**
     * 公司名称：
     */
    private String  decorationCompany;
    /**
     * 客户姓名：
     */
    private String  customerName;
    /**
     * 联系电话：
     */
    private String customerMobile;
    /**
     * 设计师：
     */
    private String designerName;

    /**
     * 详细地址：
     */
    private String address;

    /**
     * 房屋面积：
     */
    private String area;

    /**
     * 房型：
     */
    private String houseType;

    /**
     * 报价时间：
     */
    private String quoteTime;

    /**
     * 装修类型：
     */
    private String decorationType;

    /**
     * 报价类型：
     */
    private String quoteType;



}
