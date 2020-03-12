package com.juran.quote.resource.v1;

import com.alibaba.fastjson.JSONObject;
import com.juran.core.exception.ParentException;
import com.juran.core.exception.bean.ErrorMsgBean;
import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.dto.QuoteDetailDto;
import com.juran.quote.bean.enums.ExportExcelEnums;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.request.ConstCalcRequest;
import com.juran.quote.bean.request.CreateQuoteRequestBean;
import com.juran.quote.bean.request.PutQuoteBaseInfoReqBean;
import com.juran.quote.bean.response.*;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.QuoteService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/27 16:14
 * @description
 */
@Api(value = "报价流程服务")
@RestLog
@Component
@Path("/v1/quote")
@Produces(MediaType.APPLICATION_JSON)
public class QuoteResource {
    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @Autowired
    private QuoteService quoteService;

    @Autowired
    QuoteConfig quoteConfig;

    @ApiOperation(value = "获取报价-客户房屋信息", notes = "获取报价-客户房屋信息")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "客户房屋信息", response = QuoteBaseInfoRespBean.class)})
    @GET
    @Path("/{quoteId}/baseInfo")
    @LogAnnotation
    public Response getBaseInfo(@ApiParam(value = "报价编号") @PathParam("quoteId") Long quoteId) {
        if (quoteId.equals(0l)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            CommonRespBean result = quoteService.getQuoteBaseInfo(quoteId);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (QuoteException e) {
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }


    @ApiOperation(value = "更新报价基本信息", notes = "更新报价基本信息")
    @PUT
    @Path("/baseInfo")
    @LogAnnotation
    public Response updateBaseInfo(@ApiParam(value = "客户房屋信息") PutQuoteBaseInfoReqBean req, @Context ContainerRequestContext context) {
        if (req == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            String tokenValue = "";
            if (context.getCookies() != null) {
                Cookie cookie = context.getCookies().get(quoteConfig.getTokenName());
                if (cookie != null) {
                    tokenValue = context.getCookies().get(quoteConfig.getTokenName()).getValue();
                }
            }
            CommonRespBean response = quoteService.updateQuoteBaseInfo(req, tokenValue);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (QuoteException e) {
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }


    @ApiOperation(value = "获得详细报价信息", notes = "获得详细报价信息")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "获得详细报价信息成功")})
    @GET
    @Path("/{quoteId}/detail")
    @LogAnnotation
    public Response getQuoteDetailInfo(@ApiParam(value = "报价编号") @PathParam("quoteId") Long quoteId) {
        if (quoteId.equals(0l)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            CommonRespBean respBean = quoteService.getQuoteDetailDto(quoteId);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }

    @ApiOperation(value = "更新报价-清单详情", notes = "更新报价-清单详情")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "获得详细报价信息成功")})
    @PUT
    @Path("/detail")
    @LogAnnotation
    public Response updateQuoteDetailInfo(QuoteDetailDto reqBean) {
        try {
            if (reqBean.getQuoteId() == null && reqBean == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("传入参数不能为空")).build();
            }
            CommonRespBean respBean = quoteService.updateQuoteDetailInfo(reqBean);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            logger.error("更新报价-清单详情异常", e);
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }

    @ApiOperation(value = "获取报价-汇总确认信息", notes = "获取报价-汇总确认信息")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "汇总确认信息", response = GetQuoteSummaryRespBean.class)})
    @GET
    @Path("/{quoteId}/summary")
    @LogAnnotation
    public Response getQuoteSummaryInfo(@ApiParam(value = "报价编号") @PathParam("quoteId") Long quoteId) {
        try {
            CommonRespBean quoteSummary = quoteService.getQuoteSummary(quoteId);
            return Response.status(Response.Status.OK).entity(quoteSummary).build();
        } catch (QuoteException e) {
            logger.error("报价ID:{}获取报价结果异常", quoteId, e);
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }

    @ApiOperation(value = "获取个性化报价结果", notes = "获取个性化报价结果")
    @GET
    @Path("/{quoteId}/result")
    @LogAnnotation
    public Response getQuoteResult(@ApiParam(value = "报价编号") @PathParam("quoteId") Long quoteId) {
        try {
            CommonRespBean respBean = quoteService.getQuoteResult(quoteId);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            logger.error("报价ID:{}获取报价结果异常", quoteId, e);
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }


    @ApiOperation(value = "结束报价", notes = "结束报价")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "获得详细报价信息成功")})
    @POST
    @Path("/{quoteId}/endQuote")
    public Response endQuote(@ApiParam(value = "报价编号") @PathParam("quoteId") Long quoteId) {
        try {
            CommonRespBean respBean = quoteService.submitQuote(quoteId);
            return Response.status(Response.Status.OK).entity(respBean).build();
        } catch (QuoteException e) {
            logger.error("结束报价失败", e);
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }


    @ApiOperation(value = "查询家纺（软装）", notes = "查询家纺（软装）")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "家纺（软装）信息", response = QuoteRoomTextileRespBean.class)})
    @GET
    @Path("/{quoteRoomId}/textile/all")
    @LogAnnotation
    /**
     * 第三步查询家纺（软装）
     */
    public Response queryMaterial(@ApiParam(value = "房间编号") @PathParam("quoteRoomId") Long quoteRoomId,
                                  @ApiParam(value = "家纺名称") @QueryParam("textileName") String textileName,
                                  @ApiParam(value = "偏移量") @QueryParam("offset") long offset,
                                  @ApiParam(value = "标识（630）") @QueryParam("tag") String tag,
                                  @ApiParam(value = "条数") @QueryParam("limit") long limit) {
        if (quoteRoomId.equals(0l)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            HttpResponseList<TextilesRespBean> textilesRespBeanList = quoteService.getTextileByName(quoteRoomId, textileName, tag, offset, limit);
            return Response.status(Response.Status.OK).entity(textilesRespBeanList).build();
        } catch (QuoteException e) {
            logger.error("房间ID:获取全部软装信息", quoteRoomId, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation(value = "开始报价")
    @POST
    @Path("/")
    @LogAnnotation
    public Response startQuote(CreateQuoteRequestBean reqBean) {
        Long quoteId;
        JSONObject result = new JSONObject();
        try {
            CommonRespBean verifyResponse = quoteService.verifyDesignCase(reqBean);
            if (!CommonRespBean.Status.SUCCESS.equals(verifyResponse.getStatus().getCode())) {
                result.put("status", false);
                result.put("message", verifyResponse.getStatus().getMessage());
                return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
            }
            quoteId = quoteService.createQuote(reqBean);
        } catch (QuoteException e) {
            result.put("status", false);
            result.put("message", "创建报价异常:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toJSONString()).build();
        }
        if (quoteId == null) {
            result.put("status", false);
            result.put("message", "创建报价失败");
            return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
        } else {
            result.put("status", true);
            result.put("message", "创建报价成功");
            result.put("data", quoteId);
            return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
        }
    }

    @ApiOperation("案例审核，通过案例id获取报价单分享页url")
    @ApiResponses({@ApiResponse(code = 400, message = "输入参数有误", response = ErrorMsgBean.class),
            @ApiResponse(code = 200, message = "报价单分享页url", response = CommonRespBean.class)})
    @GET
    @Path("/share/{designIds}")
    @LogAnnotation
    public Response queryQuoteShareUrl(@ApiParam("案例id集合") @PathParam("designIds") String designIds) {
        List<QuoteShareUrlRespBean> result = quoteService.getQuoteShareUrl(designIds);
        if (CollectionUtils.isEmpty(result)) {
            logger.info("designIds :{} , designs is not quote!", designIds);
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @ApiOperation("验证3D方案是否存在小于1平米的房间, 返回结果中status：0 存在，3：不存在")
    @GET
    @Path("/{quoteId}/room")
    @LogAnnotation
    public Response verifyQuoteRooms(@ApiParam("报价id") @PathParam("quoteId") Long quoteId) {
        try {
            CommonRespBean result = quoteService.verifyDesignRooms(quoteId);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (QuoteException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }

    @ApiOperation("报价流程添加施工项列表")
    @GET
    @Path("/{quoteId}/construct")
    @LogAnnotation
    public Response getConstructList(@PathParam("quoteId") Long quoteId,
                                     @ApiParam("constructName") @QueryParam("constructName") String constructName) {
        try {
            CommonRespBean result = quoteService.getConstructList(quoteId, constructName);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (QuoteException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }

    @ApiOperation("动态计算主材带出的施工项数量")
    @POST
    @Path("/construct/quantity")
    @LogAnnotation
    public Response getConstructQuantity(ConstCalcRequest source) {
        try {
            CommonRespBean result = quoteService.getConstructQuantity(source);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }

    @ApiOperation("报价流程导出excel")
    @GET
    @Path("/{quoteId}/export")
    @LogAnnotation
    public Response exportQuote(
            @Context ContainerRequestContext context,
            @PathParam("quoteId") Long quoteId,
            @ApiParam("exportCode:1.全屋定制报价;2.施工报价;3材料报价;4.硬装报价;5.软装报价") @QueryParam("exportCode") Integer exportCode
    ) {
        try {
            ExportExcelEnums exportType = ExportExcelEnums.getEnumByCode(exportCode);
            if (exportType == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("导出类型不支持")).build();
            }
            String fileName = HttpUtils.encodeFileNameByAgent(context, exportType.getFileName());
            CommonRespBean result = quoteService.exportQuote(quoteId, exportType);
            if (result.getData() == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new CommonRespBean<>("生成excel出错")).build();
            }
            return Response.ok(result.getData(), MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", "attachment;filename=" + fileName).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        } catch (Exception e1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
