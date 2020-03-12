package com.juran.quote.bean.quote;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间信息
 */
@Data
public class RoomDto implements Serializable{
    private static final long serialVersionUID = 6127548697930904568L;

    /**
     * 空间ID
     */
    private String roomId;
    /**
     * 空间名称
     */
    private String roomName;

    public RoomDto() {
    }

    public RoomDto(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
