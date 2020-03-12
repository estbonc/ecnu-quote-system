package com.juran.quote.resource.v1;

import com.juran.core.exception.ParentException;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.request.CommentBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.service.CommentService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @Author: xiongtao
 * @Date: 14/09/2018 1:55 PM
 * @Description: 评论建议相关resouce
 * @Email: xiongtao@juran.com.cn
 */
@Api(value = "评论反馈服务")
@RestLog
@Component
@Path("/v1/comment")
public class CommentResource {

    @Autowired
    CommentService commentService;

    @Autowired
    QuoteConfig quoteConfig;

    @POST
    @Path("/")
    @ApiOperation(value = "新增评论")
    @LogAnnotation
    public Response createComment(CommentBean source, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            source.setCreateBy(userName);
            CommonRespBean serviceResponse = commentService.createComment(source);
            return Response.status(Response.Status.CREATED)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }
}
