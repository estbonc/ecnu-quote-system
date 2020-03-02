package com.ecnu.paper.quotesystem.bean.response;

import com.juran.quote.bean.quote.RoomDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 报价-房型信息
 */
@Data
public class GetQuoteHouseInfoRespBean implements Serializable {
    /**
     * 房型类型
     */
    private String houseType;
    /**
     * 房型名称
     */
    private String houseName;
    /**
     * 房型-空间信息
     */
    private List<RoomDto> roomList;


    public GetQuoteHouseInfoRespBean() {
    }

    public GetQuoteHouseInfoRespBean(String houseType, String houseName) {
        this.houseName = houseName;
        this.houseType = houseType;
    }
}
