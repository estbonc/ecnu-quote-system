package com.juran.quote.resource.v1;

import com.juran.core.exception.ParentException;
import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.request.DecorationTypeRequestBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.DecorationTypeService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import com.juran.quote.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "装修类型服务")
@RestLog
@Component
@Path("/v1/decorationType")
@Produces(MediaType.APPLICATION_JSON)
public class DecorationTypeResource {
    @Autowired
    QuoteConfig quoteConfig;

    @Autowired
    DecorationTypeService decorationTypeService;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @POST
    @Path("/")
    @ApiOperation(value = "创建装修类型")
    @LogAnnotation
    public Response createDecorationType(DecorationTypeRequestBean requestBean, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = decorationTypeService.createDecorationType(requestBean, userName);
            return Response.status(Response.Status.CREATED).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }


    @PUT
    @Path("/{decorationTypeId}")
    @ApiOperation(value = "修改装修类型")
    @LogAnnotation
    public Response updateDecorationType(@PathParam("decorationTypeId") Long id,
                                         DecorationTypeRequestBean requestBean,
                                         @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = decorationTypeService.updateDecorationType(id, requestBean, userName);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @DELETE
    @Path("/{decorationTypeId}")
    @ApiOperation(value = "删除装修类型")
    @LogAnnotation
    public Response deleteDecorationType(@ApiParam("id") @PathParam("decorationTypeId")Long id,
                                         @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean respBean = decorationTypeService.deleteDecorationType(id, userName);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @GET
    @Path("/")
    @ApiOperation(value = "查询装修类型")
    @LogAnnotation
    public Response getDecorationType(@ApiParam(value = "装饰公司") @QueryParam("decorationCompany") String decorationCompany,
                                      @ApiParam(value = "装修类型") @QueryParam("decorationType")String decorationType,
                                      @ApiParam(value = "状态") @QueryParam("status")Integer status){
        try {
            CommonRespBean respBean = decorationTypeService.getDecorationTypes(decorationCompany, decorationType, status);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean =
                    new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }
}
