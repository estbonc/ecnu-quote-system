package com.ecnu.paper.quotesystem.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetQuoteSummaryRespBean implements Serializable {
    private static final long serialVersionUID = 7392389250794014806L;

    private Long packageId;
    private Long packageVersionId;
    private String quoteName;
    private String quotationTime;

    /**
     * 基本报价信息
     */
    private GetQuoteSummaryPriceRespBean price;

    private GetQuoteBaseInfoRespBean customerInfo;

    /**
     * 空间主材信息
     */
    private List<GetQuoteRoomDetailRespBean> roomList;
}
