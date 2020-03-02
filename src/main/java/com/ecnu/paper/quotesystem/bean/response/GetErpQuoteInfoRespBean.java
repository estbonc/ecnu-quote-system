package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetErpQuoteInfoRespBean implements Serializable {

    private static final long serialVersionUID = 30732958075520482L;

    /**
     * 报价ID
     */
    private Long quoteId;
    /**
     * 套餐名称
     */
    private String quoteName;
    /**
     * 套餐ID
     */
    private Long packageId;
    /**
     * 套餐CODE
     */
    private String packageCode;
    /**
     * 版本ID
     */
    private Long packageVersionId;
    /**
     * 版本CODE
     */
    private String packageVersionCode;
    /**
     * 户型 ERP
     */
    private String roomType;
    /**
     * 总价信息
     */
    private ErpPriceRespBean price;

    private List<ErpQuoteItemInfoRespBean> erpList;
}
