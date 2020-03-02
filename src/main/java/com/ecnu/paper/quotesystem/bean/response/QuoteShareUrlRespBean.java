package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/8/15 16:31
 * @description
 */
@Data
public class QuoteShareUrlRespBean {

    @ApiModelProperty("案例id")
    private String designId;

    @ApiModelProperty("分享页url")
    private String shareUrl;
}
