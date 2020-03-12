package com.juran.quote.bean.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 22/10/2018 6:10 PM
 * @Description: 施工项房间带出关系
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class ConstructRoomBindDto {

    @ApiModelProperty(value = "空间id", required = true, example = "7103825")
    private Long roomId;

    @ApiModelProperty(value = "空间名称", required = true, example = "设备间")
    private String roomName;

    @ApiModelProperty("房型")
    private String roomType;
}
