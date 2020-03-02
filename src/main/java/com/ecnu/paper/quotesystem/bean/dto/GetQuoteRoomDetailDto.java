package com.ecnu.paper.quotesystem.bean.dto;

import com.juran.quote.bean.po.QuoteRoomConstructPo;
import com.juran.quote.bean.po.QuoteRoomMaterialPo;
import com.juran.quote.bean.po.QuoteRoomTextilePo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetQuoteRoomDetailDto implements Serializable {
    private static final long serialVersionUID = -7991757692371918801L;
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
     * 超量主材的总价格
     */
    private BigDecimal innerMMListOverPrice = BigDecimal.ZERO;
    /**
     * 套餐内主材
     */
    private List<QuoteRoomMaterialPo> innerMMList;
    /**
     * 套餐外主材
     */
    private List<QuoteRoomMaterialPo> outerMMList;
    /**
     * 套餐内施工项列表 空间和主材带出的
     */
    private List<QuoteRoomConstructPo> innerCIList;
    /**
     * 套餐外施工项列表 Construct主表
     */
    private List<QuoteRoomConstructPo> outerCIList;
    /**
     * 大礼包施工项列表
     */
    private List<QuoteRoomConstructPo> optionalCIList;
    /**
     * 家纺家居列表
     */
    private List<QuoteRoomTextilePo> homeTextile;
}