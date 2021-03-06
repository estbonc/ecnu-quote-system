package com.juran.quote.resource.v1;

import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.request.QuoteTypeBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.service.QuoteTypeService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "报价类型服务")
@RestLog
@Component
@Path("/v1/quoteType")
@Produces(MediaType.APPLICATION_JSON)
public class QuoteTypeResource {
    @Autowired
    QuoteConfig quoteConfig;

    @Autowired
    QuoteTypeService quoteTypeService;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @GET
    @Path("/")
    @ApiOperation(value = "查询报价类型")
    @LogAnnotation
    public Response getQuoteTypes(@QueryParam("decorationCompany") String decorationCompany,
                                  @QueryParam("decorationTypeId") Long decorationTypeId,
                                  @QueryParam("quoteType") String quoteType,
                                  @QueryParam("code") String code,
                                  @QueryParam("status") Integer status,
                                  @QueryParam("limit") @DefaultValue("15")  Integer limit,
                                  @QueryParam("offset") @DefaultValue("1")  Integer offSet) {
        try {
            CommonRespBean respBean = quoteTypeService.getQuoteTypes(decorationCompany,
                    decorationTypeId,
                    quoteType,
                    code,
                    status,
                    limit,
                    offSet);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @PUT
    @Path("/{quoteTypeId}")
    @ApiOperation(value = "更新报价类型")
    @LogAnnotation
    public Response updateQuoteTypes(@PathParam("quoteTypeId") Long quoteTypeId, QuoteTypeBean request, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = quoteTypeService.updateQuoteType(quoteTypeId, request, userName);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @POST
    @Path("/")
    @ApiOperation(value = "创建报价类型")
    @LogAnnotation
    public Response createQuoteTypes(QuoteTypeBean request, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = quoteTypeService.createQuoteType(request, userName);
            return Response.status(Response.Status.CREATED).entity(respBean).build();
        } catch (QuoteException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @DELETE
    @Path("/{quoteTypeId}")
    @ApiOperation(value = "删除报价类型")
    @LogAnnotation
    public Response deleteQuoteTypes(@PathParam("quoteTypeId") Long quoteTypeId, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = quoteTypeService.deleteQuoteTypes(quoteTypeId, userName);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }
}
