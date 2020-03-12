package com.juran.quote.resource.v1;

import com.juran.core.exception.ParentException;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.enums.ChargeCalculationTypeEnums;
import com.juran.quote.bean.request.ChargeTypeRequestBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.service.ChargeTypeService;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/11/2 14:52
 * @description
 */
@Api(value = "自定义收费类型管理")
@RestLog
@Component
@Path("/v1/chargeType")
@Produces(MediaType.APPLICATION_JSON)
public class ChargeTypeResource {


    @Autowired
    private ChargeTypeService chargeTypeService;


    @ApiOperation("查询自定义收费类型@lss")
    @GET
    @Path("/")
    @LogAnnotation
    public Response getChargeType(@ApiParam("装饰公司") @QueryParam("decorationCompany") String decorationCompany,
                                  @ApiParam("创建日期") @QueryParam("createTime") Long createTime,
                                  @ApiParam("状态") @DefaultValue("1") @QueryParam("status") Integer status,
                                  @QueryParam("offset") @DefaultValue("1") Integer offset,
                                  @QueryParam("limit") @DefaultValue("15") Integer limit) {
        try {
            CommonRespBean serviceResponse = chargeTypeService.getChargeType(decorationCompany, createTime, status, offset, limit);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("创建自定义收费类型@lss")
    @POST
    @Path("/")
    @LogAnnotation
    public Response createChargeType(ChargeTypeRequestBean requestBean) {
        try {
            if (StringUtils.isEmpty(requestBean.getDecorationCompany())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少装饰公司信息")).build();
            }
            if (StringUtils.isEmpty(requestBean.getChargeType())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少自定义费用类型信息")).build();
            }
            if(requestBean.getCalculationType() == null) {
                return  Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算方式信息")).build();
            }
            if(requestBean.getCalculationType().equals(ChargeCalculationTypeEnums.FIXED_CHARGE.getCode()) && requestBean.getAmount() == null) {
                return  Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少收费金额")).build();
            }
            if(requestBean.getCalculationType().equals(ChargeCalculationTypeEnums.RATE_CHARGE.getCode())){
                if(requestBean.getRate() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算比例")).build();
                }
                if(requestBean.getRateBase() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算比例基准")).build();
                }
            }
            CommonRespBean serviceResponse = chargeTypeService.createChargeType(requestBean);
            return Response.status(Response.Status.CREATED).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @ApiOperation("编辑自定义收费类型@lss")
    @PUT
    @Path("/{chargeTypeId}")
    @LogAnnotation
    public Response updateChargeType(ChargeTypeRequestBean requestBean, @PathParam("chargeTypeId") Long chargeTypeId) {
        try {
            if (StringUtils.isEmpty(requestBean.getDecorationCompany())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少装饰公司信息")).build();
            }
            if (StringUtils.isEmpty(requestBean.getChargeType())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少自定义费用类型信息")).build();
            }
            if(requestBean.getCalculationType() == null) {
                return  Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算方式信息")).build();
            }
            if(requestBean.getCalculationType().equals(ChargeCalculationTypeEnums.FIXED_CHARGE.getCode()) && requestBean.getAmount() == null) {
                return  Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少收费金额")).build();
            }
            if(requestBean.getCalculationType().equals(ChargeCalculationTypeEnums.RATE_CHARGE.getCode())){
                if(requestBean.getRate() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算比例")).build();
                }
                if(requestBean.getRateBase() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少计算比例基准")).build();
                }
            }
            CommonRespBean response = chargeTypeService.updateChargeType(chargeTypeId, requestBean);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @ApiOperation("删除自定义收费类型@lss")
    @DELETE
    @Path("/{chargeTypeId}")
    @LogAnnotation
    public Response deleteChargeType(@PathParam("chargeTypeId") Long chargeTypeId) {
        try {
            CommonRespBean response = chargeTypeService.deleteChargeType(chargeTypeId);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @ApiOperation("校验自定义费用类型是否存在")
    @GET
    @Path("/check/{chargeType}")
    @LogAnnotation
    public Response checkChargeType(@PathParam("chargeType") String chargeType,
                                    @ApiParam("装饰公司") @QueryParam("decorationCompany") String decorationCompany) {
        try {
            if (StringUtils.isEmpty(decorationCompany)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("缺少装饰公司信息")).build();
            }
            CommonRespBean serviceResponse = chargeTypeService.checkChargeType(chargeType, decorationCompany);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

}
