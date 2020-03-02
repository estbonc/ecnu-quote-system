package com.ecnu.paper.quotesystem.resource;

import com.alibaba.fastjson.JSONObject;
import com.juran.core.exception.ParentException;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.po.PackageRoom;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.service.PackageRoomService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "空间管理服务")

@Component
@Path("/v1/room")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    @Autowired
    PackageRoomService roomService;

    @Autowired
    QuoteConfig quoteConfig;

    @GET
    @Path("/roomTypes")
    @ApiOperation(value = "查询数据库所有房型")
    @LogAnnotation
    public Response queryDistinctRoom(@QueryParam("houseType") String houseType) {
        List<String> roomList = roomService.queryAllRoomTypes(houseType);
        return Response.status(Response.Status.OK).entity(roomList).build();
    }

    @POST
    @Path("/single")
    @ApiOperation(value = "查询单个套餐空间")
    @LogAnnotation
    public Response querySingleRoom(@RequestBody PackageRoomRequestBean requestBean) {
        JSONObject result = new JSONObject();
        PackageRoom room = roomService.querySinglePackageRoom(requestBean);
        if (room != null) {
            result.put("packageRoomId", room.getPackageRoomId());
        }
        return Response.status(Response.Status.OK).entity(result.toJSONString()).build();
    }


    @POST
    @Path("/space")
    @ApiOperation(value = "新增空间")
    @LogAnnotation
    public Response createSpace(RoomBean source, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse  = roomService.createRoom(source);
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
    @Path("/space/{id}")
    @ApiOperation(value = "删除空间")
    @LogAnnotation
    public Response deleteRoom(@PathParam("id") Long id, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean serviceResponse = roomService.deleteRoom(id, userName);
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
    @ApiOperation(value = "查询空间列表空间")
    @LogAnnotation
    public Response QueryRoom() {

        try {
            CommonRespBean serviceResponse  = roomService.queryAllRoom();
            return Response.status(Response.Status.OK)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


    @POST
    @Path("/houseType")
    @ApiOperation(value = "新增户型")
    @LogAnnotation
    public Response createHouseType(HouseTypeBean source, @HeaderParam("User") String user) {
        try {
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse  = roomService.createHouseType(source);
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
    @Path("/houseType/{id}")
    @ApiOperation(value = "删除户型")
    @LogAnnotation
    public Response deleteHouseType(@PathParam("id") Long id, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean serviceResponse = roomService.deleteHouseType(id, userName);
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
    @Path("/houseType/{id}")
    @ApiOperation(value = "更新户型")
    @LogAnnotation
    public Response updateHouseType(@PathParam("id") Long id, HouseTypeBean source, @HeaderParam("User") String user) {
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse  = roomService.updateHouseType(id,source);
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
    @Path("/houseType")
    @ApiOperation(value = "列表条件查询房型")
    @LogAnnotation
    public Response queryHouseType(@BeanParam QueryHouseTypeBean source) {
        try {
            PageRespBean<HouseTypeBean> response = roomService.queryHouseType(source);
            CommonRespBean commonRespBean = new CommonRespBean(response.getData(), new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询户型列表成功"),
                    response);
            return Response.status(Response.Status.OK)
                    .entity(commonRespBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }








}
