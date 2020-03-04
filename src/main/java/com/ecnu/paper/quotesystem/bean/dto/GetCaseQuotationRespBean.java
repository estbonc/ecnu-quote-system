package com.ecnu.paper.quotesystem.bean.dto;

import com.ecnu.paper.quotesystem.bean.response.GetQuoteSummaryPriceRespBean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCaseQuotationRespBean implements Serializable {

    @ApiModelProperty("报价Id")
    private Long quoteId;

    @ApiModelProperty("3D-设计方案ID")
    private String designId;

    @ApiModelProperty("报价单分享url")
    private String shareUrl;

    @ApiModelProperty("报价单创建时间")
    private Long createTime;

    @ApiModelProperty("报价单修改时间")
    private Long updateTime;

    @ApiModelProperty("基本报价信息")
    private GetQuoteSummaryPriceRespBean price;

    @ApiModelProperty("空间主材信息")
    private List<QuoteRoomDetailDto> roomList;

}
