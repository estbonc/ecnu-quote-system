package com.juran.quote.bean.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 14/09/2018 2:17 PM
 * @Description: 评论反馈的Po
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Document(collection = "comment")
public class Comment {

    @Id
    private String id;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 报价的id
     */
    private Long quoteId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 操作截图
     */
    private List<String> images;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户
     */
    private String createBy;

    /**
     * 评论时间
     */
    private Date createTime;
}
