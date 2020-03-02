package com.ecnu.paper.quotesystem.bean.dto;

import lombok.Data;

@Data
public class BindRoom {
    /**
     * 房间id
     */
    private Long  roomId;

    /**
     * 房间类型中文
     */
    private String roomName;

    /**
     * 房间类型英文
     */
    private String englishName;

    /**
     * 支持的户型
     */
    private Long houseTypeId;

}
