package com.juran.quote.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetQuoteSpaceMappingRespBean implements Serializable {
    private static final long serialVersionUID = 2306260730162152079L;
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
