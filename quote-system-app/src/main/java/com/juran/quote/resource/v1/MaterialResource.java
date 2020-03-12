package com.juran.quote.resource.v1;

import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.po.SelectedMaterial;
import com.juran.quote.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "主材管理服务")
@RestLog
@Component
@Path("/v1/material")
@Produces(MediaType.APPLICATION_JSON)
public class MaterialResource {
    @Autowired
    MaterialService materialService;

    @GET
    @Path("/types")
    @ApiOperation(value = "查询所有主材类型")
    public Response queryDistinctMaterial() {
        List<SelectedMaterial> materialList = materialService.queryDistinctMaterial();
        return Response.status(Response.Status.OK).entity(materialList).build();
    }
}
