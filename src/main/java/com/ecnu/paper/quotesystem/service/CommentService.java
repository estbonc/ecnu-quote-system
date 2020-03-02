package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.juran.quote.bean.po.Comment;
import com.juran.quote.bean.request.CommentBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.utils.KeyManagerUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: xiongtao
 * @Date: 14/09/2018 2:14 PM
 * @Description: 评论反馈的service
 * @Email: xiongtao@juran.com.cn
 */
@Service
public class CommentService extends  BaseService {

    @Autowired
    KeyManagerUtil keyManagerUtil;

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 新增评论
     * @param source
     * @return
     */
    public CommonRespBean createComment(CommentBean source) throws PackageException {
        Comment comment = new Comment();
        try {
            BeanUtils.copyProperties(source, comment);
            comment.setCommentId(keyManagerUtil.getUniqueId());
            comment.setCreateTime(new Date());
            mongoTemplate.insert(comment);
        } catch (BeansException e) {
            logger.error("新增评论{}出现异常{}", JSON.toJSONString(comment),e.getMessage());
            throw new PackageException("新增评论出现异常");
        } catch (RuntimeException e) {
            logger.error("新增评论{}出现异常{}",JSON.toJSONString(comment), e.getMessage());
            throw new PackageException("新增评论出现异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "新增评论成功"));



    }
}
