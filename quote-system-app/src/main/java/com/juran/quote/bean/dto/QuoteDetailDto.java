package com.juran.quote.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class QuoteDetailDto implements Serializable {
    private static final long serialVersionUID = 6568699642010926816L;

    @ApiModelProperty(value = "报价id", example = "504681282754107")
    private Long quoteId;

    @ApiModelProperty("房间列表")
    private List<QuoteRoomDto> roomList;

    @ApiModelProperty("额外收费")
    private List<ChargeTypeDto> extraCharges;
}
