package com.juran.quote.bean.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 3D工具-bom清单-房间
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BomRoomDto implements Serializable {
    private static final long serialVersionUID = -8060769297624932773L;
    //3D带出的roomID
    private String roomID;
    //房间类型
    private String roomType;
    //3D设计上的房间名称
    private String roomName;
    //面积
    private String area;
    //周长
    private String perimeter;
    //房高
    private String height;
}
