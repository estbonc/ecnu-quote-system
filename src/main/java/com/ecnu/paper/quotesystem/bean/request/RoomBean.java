package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 3:03 PM
 * @Description: 空间vo
 * @Email: xiongtao@juran.com.cn
 */
@ApiModel
@Data
public class RoomBean extends BaseBean{


    @ApiModelProperty(value = "空间id", required = false, example = "123456")
    private Long roomId;

    @ApiModelProperty(value = "空间名称", required = true, example = "主卧")
    private String roomName;

    @ApiModelProperty(value = "空间英文名称", required = true, example = "LivingRoom")
    private String englishName;

    @ApiModelProperty(value = "空间状态", required = false, example = "1")
    private Integer status;
}
