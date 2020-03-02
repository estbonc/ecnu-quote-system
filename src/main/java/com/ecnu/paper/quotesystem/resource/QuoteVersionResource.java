package com.ecnu.paper.quotesystem.resource;

import com.juran.core.exception.ParentException;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.request.QueryQuoteVersionBean;
import com.juran.quote.bean.request.QuoteVersionBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.QuoteVersionService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author: xiongtao
 * @Date: 09/10/2018 1:14 PM
 * @Description: 报价版本管理
 * @Email: xiongtao@juran.com.cn
 */
@Api(value = "报价版本管理服务")

@Component
@Path("/v1/quoteVersion")
@Produces(MediaType.APPLICATION_JSON)
public class QuoteVersionResource {

    @Autowired
    QuoteConfig quoteConfig;

    @Autowired
    QuoteVersionService quoteVersionService;


    @POST
    @Path("/")
    @ApiOperation(value = "新增报价版本")
    @LogAnnotation
    public Response createQuoteVersion(QuoteVersionBean source, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            source.setUpdateBy(HttpUtils.decodeUser(user));
            CommonRespBean serviceResponse = quoteVersionService.createQuoteVersion(source);
            return Response.status(Response.Status.OK)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @ApiOperation(value = "更新报价版本")
    @LogAnnotation
    public Response updateQuoteVersion(QuoteVersionBean source, @HeaderParam("User") String user, @PathParam("id") Long id) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            source.setUpdateBy(HttpUtils.decodeUser(user));
            CommonRespBean serviceResponse = quoteVersionService.updateQuoteVersion(source, id);
            return Response.status(Response.Status.OK)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "删除报价版本")
    @LogAnnotation
    public Response deleteQuoteVersion(@PathParam("id") Long id, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            CommonRespBean serviceResponse = quoteVersionService.deleteQuoteVersion(id, HttpUtils.decodeUser(user));
            return Response.status(Response.Status.OK)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @GET
    @Path("/")
    @ApiOperation(value = "报价版本列表查询")
    @LogAnnotation
    public Response queryQuoteVersion(@BeanParam QueryQuoteVersionBean source) {
        try {
            PageRespBean<QuoteVersionBean> response = quoteVersionService.queryQuoteVersionList(source);
            CommonRespBean commonRespBean = new CommonRespBean(response.getData(), new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询套餐施工项成功"),
                    response);
            return Response.status(Response.Status.OK)
                    .entity(commonRespBean)
                    .build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }

}
