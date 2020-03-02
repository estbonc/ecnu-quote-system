package com.ecnu.paper.quotesystem.resource;

import com.juran.core.exception.ParentException;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.request.DictionaryBean;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.service.DictionaryService;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author: xiongtao
 * @Date: 10/09/2018 6:24 PM
 * @Description: 字典查询
 * @Email: xiongtao@juran.com.cn
 */

@Api(value = "字典服务")

@Component
@Path("/v1/dictionary")
@Produces(MediaType.APPLICATION_JSON)
public class DictionaryResource {


    @Autowired
    DictionaryService dictionaryService;

    @GET
    @Path("/")
    @ApiOperation(value = "根据type查询字典内容",notes="type=>construct_category:施工项类别,construct_item:施工项分类,unit:计量单位,calculator_variable:施工项计算变量")
    @LogAnnotation
    public Response findFieldByType(@ApiParam(value = "字段的type")@QueryParam("type") String type) {
        try {
            CommonRespBean serviceResponse = dictionaryService.findFieldByType(type);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


    @POST
    @Path("/")
    @ApiOperation(value = "新增字典内容")
    @LogAnnotation
    public Response insertField(DictionaryBean dictionaryBean) {
        try {
            CommonRespBean serviceResponse = dictionaryService.insertDictionary(dictionaryBean);
            return Response.status(Response.Status.CREATED).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }


    }


}
