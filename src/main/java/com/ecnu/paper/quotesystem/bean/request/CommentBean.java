package com.ecnu.paper.quotesystem.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 14/09/2018 2:08 PM
 * @Description: 反馈评论的vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel("反馈评论的vo")
public class CommentBean {


    @ApiModelProperty(value = "评论的id", required = false, example = "123456")
    private Long commentId;

    @ApiModelProperty(value = "对应的报价的id", required = false, example = "123456")
    private Long quoteId;

    @ApiModelProperty(value = "评论内容", required = true, example = "这个报价流程真牛逼")
    private String content;

    @ApiModelProperty(value = "操作截图的url", required = false)
    private List<String> images;

    @ApiModelProperty(value = "用户邮箱", required = false, example = "bigapple@juran.com.cn")
    private String email;

    @ApiModelProperty(value = "用户", required = false, example = "lisiqi")
    private String createBy;

    @ApiModelProperty(value = "评论时间", required = false)
    private Date createTime;

}
