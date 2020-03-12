package com.juran.quote.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 获得报价房间的基本信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GetQuoteRoomDetailRespBean implements Serializable {
    private static final long serialVersionUID = 3316947861071288615L;
    /**
     * 房间ID
     */
    private String roomId;
    /**
     * 房间名称
     */
    private String roomName;
    /**
     * 房间类型
     */
    private String roomType;
    /**
     * 每个房间软装金额
     */
    private BigDecimal eachSoftPrice;

    /**
     * 套餐内主材
     */
    private List<GetQuoteMaterialDetailRespBean> innerMMList;
    /**
     * 套餐外主材
     */
    private List<GetQuoteMaterialDetailRespBean> outerMMList;
    /**
     * 套餐内施工项列表
     */
    private List<GetQuoteCIDetailRespBean> innerCIList;
    /**
     * 套餐外施工项列表
     */
    private List<GetQuoteCIDetailRespBean> outerCIList;
    /**
     * 大礼包施工项列表
     */
    private List<GetQuoteCIDetailRespBean> optionalCIList;

    /**
     * 家纺家居列表
     */
    private List<QuoteRoomTextileRespBean> homeTextile;
}
