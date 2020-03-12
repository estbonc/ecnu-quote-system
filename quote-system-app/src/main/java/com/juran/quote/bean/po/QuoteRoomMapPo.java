package com.juran.quote.bean.po;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class QuoteRoomMapPo {
    /**
     * 3D房间ID
     */
    private String designRoomId;
    /**
     * 3D房间名称
     */
    private String designRoomName;
    /**
     * 套餐内房间ID
     */
    private String roomId;
    /**
     * 套餐内房间名称
     */
    private String roomName;
    /**
     * 面积
     */
    private BigDecimal roomSize;
}
