package com.juran.quote.bean.dto;

import com.juran.quote.bean.po.QuoteRoomConstructPo;
import com.juran.quote.bean.po.QuoteRoomMaterialPo;
import com.juran.quote.bean.po.QuoteRoomTextilePo;
import com.juran.quote.bean.response.GetQuoteBaseInfoRespBean;
import com.juran.quote.bean.response.QuoteBaseInfoRespBean;
import com.juran.quote.bean.response.GetQuoteSummaryPriceRespBean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetQuoteSummaryDto implements Serializable {
    private static final long serialVersionUID = 1711876066417359866L;
    /**
     * 报价ID
     */
    private Long quoteId;
    /**
     * 3D-设计方案ID
     */
    private String designId;
    private Long packageId;
    private Long packageVersionId;
    private String quoteName;
    private  String quotationTime;
    /**
     * 报价单创建时间
     */
    private String quotationGmtCreate;
    /**
     * 报价单修改时间
     */
    private String quotationGmtModify;
    /**
     * 基本报价信息
     */
    private GetQuoteSummaryPriceRespBean price;

    private GetQuoteBaseInfoRespBean customerInfo;
    /**
     * 空间主材信息
     */
    private List<GetQuoteRoomDetailDto> roomList;
    /**
     * 套餐内主材项
     */
    private List<QuoteRoomMaterialPo> innerMaterialsItems;
    /**
     * 超量的主材项
     */
    private List<QuoteRoomMaterialPo> overLimitMaterialsItems;
    /**
     * 套餐外主材项
     */
    private List<QuoteRoomMaterialPo> outerMaterialsItems;
    /**
     * 施工项
     */
    private List<QuoteRoomConstructPo> constructionItems;
    /**
     * 超量的施工项
     */
    private List<QuoteRoomConstructPo> ciOverLimit;
    /**
     * 大礼包施工项
     */
    private List<QuoteRoomConstructPo> ciGifts;
    /**
     * 自定义施工项
     */
    private List<QuoteRoomConstructPo> ciManual;

    /**
     * 家纺家居列表
     */
    private List<QuoteRoomTextilePo> homeTextiles;

}
