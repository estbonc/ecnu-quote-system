package com.juran.quote.bean.po;

import com.juran.quote.bean.dto.ErpQuoteItemInfo;
import com.juran.quote.bean.response.ErpPriceRespBean;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(collection = "quoteFinallyResultPo")
@Data
public class QuoteFinallyResultPo {
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
    /**
     * ERP列表
     */
    private List<ErpQuoteItemInfo> erpList;

}
