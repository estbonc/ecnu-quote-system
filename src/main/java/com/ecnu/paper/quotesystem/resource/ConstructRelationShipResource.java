package com.ecnu.paper.quotesystem.resource;

import com.ecnu.paper.quotesystem.bean.exception.ParentException;
import com.ecnu.paper.quotesystem.bean.request.ConstructRelationshipRequestBean;
import com.ecnu.paper.quotesystem.bean.request.QueryConstructRelationShipBean;
import com.ecnu.paper.quotesystem.bean.response.CommonRespBean;
import com.ecnu.paper.quotesystem.service.ConstructRelationShipService;
import com.ecnu.paper.quotesystem.utils.HttpUtils;
import com.ecnu.paper.quotesystem.utils.LogAnnotation;
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
 * @Date: 22/10/2018 11:28 AM
 * @Description: 施工项带出关系维护相关接口
 * @Email: xiongtao@juran.com.cn
 */
@Api(value = "施工项带出关系相关")
@Component
@Path("/v1/relationShip")
@Produces(MediaType.APPLICATION_JSON)
public class ConstructRelationShipResource {

    @Autowired
    ConstructRelationShipService constructRelationShipService;


    @POST
    @Path("/")
    @ApiOperation(value = "更新施工项带出关系")
    @LogAnnotation
    public Response updateConstructRelationShip(ConstructRelationshipRequestBean source, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = constructRelationShipService.updateRelationShip(source);
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
    @Path("/space")
    @ApiOperation(value = "查询施工项带出关系-空间", notes = "查询施工项带出关系")
    @LogAnnotation
    public Response findSpaceConstructRelationShip(@BeanParam QueryConstructRelationShipBean source) {
        try {
            CommonRespBean serviceResponse = constructRelationShipService.findSpaceConstructRelationShip(source);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @GET
    @Path("/material")
    @ApiOperation(value = "查询施工项带出关系-主材", notes = "查询施工项带出关系")
    @LogAnnotation
    public Response findMaterialConstructRelationShip(@BeanParam QueryConstructRelationShipBean source) {
        try {
            CommonRespBean serviceResponse = constructRelationShipService.findMaterialConstructRelationShip(source);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


}
