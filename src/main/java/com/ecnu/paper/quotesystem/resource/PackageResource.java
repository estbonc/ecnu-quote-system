package com.ecnu.paper.quotesystem.resource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.juran.core.exception.ParentException;
import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.enums.PackageError;
import com.juran.quote.bean.po.StorePo;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PackageVersionResponseBean;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.*;
import com.juran.quote.utils.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(value = "套餐管理服务")

@Component
@Path("/v1/package")
@Produces(MediaType.APPLICATION_JSON)
public class PackageResource {

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);
    @Autowired
    PackageService packageService;

    @Autowired
    PackageBagService packageBagService;

    @Autowired
    PackageRoomService packageRoomService;

    @Autowired
    PackagePriceService packagePriceService;

    @Autowired
    PackageConstructService packageConstructService;

	@Autowired
    MaterialService materialService;

    @ApiOperation(value = "创建套餐")
    @POST
    @Path("/")
    @LogAnnotation
    public Response createPackage(@ApiParam(required = true) PackageRequestBean pkg) {

        try {
            CommonRespBean respBean = packageService.createPackage(pkg);
            return Response.status(Response.Status.CREATED).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean).build();
        }
    }

    @ApiOperation(value = "删除套餐")
    @DELETE
    @Path("/{packageId}")
    @LogAnnotation
    public Response removePackage(@PathParam("packageId")Long packageId) {
        try {
            packagePriceService.removePackagePriceByPackage(packageId);
            packageConstructService.removePackageConstructByPackage(packageId);
            packageRoomService.removePackageRoomByPackage(packageId);
            packageService.removePackageVersionByPackage(packageId);

        } catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }

        try {
            CommonRespBean respBean = packageService.removePackage(packageId);
            return Response.status(Response.Status.OK).entity(respBean).build();
        }
        catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("获取套餐信息")
    @LogAnnotation
    @GET
    @Path("/")
    public Response getPackages(@ApiParam("套餐名称") @QueryParam("packageName") String pkgName,
                                @ApiParam("套餐状态") @QueryParam("status") Integer status){

        Map<String, Object> queryParmas = Maps.newHashMap();
        if (!StringUtils.isEmpty(pkgName)) queryParmas.put("packageName", pkgName);
        if(status != null) queryParmas.put("status", status);
        List<Package> packageList = packageService.getAllPackagesWithFilter(queryParmas);
        CommonRespBean respBean =
                new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取套餐信息成功"),
                        packageList);
        return Response.status(Response.Status.OK)
                .entity(respBean)
                .build();
    }

    @ApiOperation("获取套餐版本")
    @GET
    @LogAnnotation
    @Path("/version")
    public Response getPackageVersionById(@ApiParam("套餐版本") @QueryParam("version") String version,
                                          @ApiParam("适用套餐名称") @QueryParam("packageId") Long packageId,
                                          @ApiParam("生效日期") @QueryParam("startTime") Long startTime,
                                          @ApiParam("失效日期") @QueryParam("endTime") Long endTime,
                                          @ApiParam("区域") @QueryParam("region") String region,
                                          @ApiParam("门店") @QueryParam("store") String store){

        List<PackageVersionResponseBean> response = packageService.queryPkgVersion(version,
                packageId, startTime, endTime,region,store, null);
        CommonRespBean respBean =
                new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取套餐版本成功"),
                        response);
        return Response.status(Response.Status.OK)
                .entity(respBean)
                .build();
    }

    @ApiOperation("根据id获取套餐版本")
    @GET
    @LogAnnotation
    @Path("/version/{packageVersionId}")
    public Response getPackageVersionById(@ApiParam(required = true) @PathParam("packageVersionId") Long pkgVersionId) {
        List<PackageVersionResponseBean> version = packageService.queryPkgVersion(null,
                null,
                null,
                null,
                null,
                null,
                pkgVersionId);
        CommonRespBean respBean =
                new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取套餐版本成功"),
                        version);
        return Response.status(Response.Status.OK)
                .entity(respBean)
                .build();
    }

    @ApiOperation(value = "创建套餐版本")
    @POST
    @Path("/version")
    @LogAnnotation
    public Response createPackageVersion(@ApiParam(required = true)PackageVersionRequestBean pkgVersion) {
        try {
            CommonRespBean respBean = packageService.createPackageVersion(pkgVersion);
            return Response.status(Response.Status.CREATED).entity(respBean).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation(value = "删除套餐版本")
    @POST
    @Path("/version/{packageVersionId}")
    @LogAnnotation
    public Response removePackageVersion(@PathParam("packageVersionId") Long pkgVersionId){
        try {
            packagePriceService.removePackagePriceByPkgVersion(pkgVersionId);
            packageConstructService.removePackageConstructByPkgVersion(pkgVersionId);
            packageRoomService.removePackageRoomByPkgVersion(pkgVersionId);
            packageService.removePackageByPackageVersion(pkgVersionId);
        } catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }

        try {
            CommonRespBean respBean = packageService.removePackageVersion(pkgVersionId);
            return Response.status(Response.Status.OK).entity(respBean).build();
        }
        catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION,e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation(value = "创建套餐礼包")
    @POST
    @Path("/bag")
    @LogAnnotation
    public Response createPackageBag(@ApiParam(required = true)PackageBagRequestBean bag) {
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packageBagService.createPackageBag(bag);
            returnObj.put(serviceResponse.getStatus().toString(), serviceResponse.getMessage());
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        return Response.status(Response.Status.CREATED).entity(returnObj).build();
    }

    @ApiOperation(value = "删除套餐礼包")
    @DELETE
    @Path("/bag/{bagId}")
    @LogAnnotation
    public Response removePackageBag(@PathParam("bagId")Long bagId) {
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packageBagService.removePackageBag(bagId);
            if(!serviceResponse.getStatus()) {
                returnObj.put("failed", serviceResponse.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(returnObj).build();
            }
            returnObj.put("success", serviceResponse.getMessage());
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        returnObj.put("packageBagId", bagId);
        return Response.status(Response.Status.OK).entity(returnObj).build();
    }


    @ApiOperation(value = "创建套餐空间")
    @POST
    @Path("/room")
    @LogAnnotation
    public Response createPackageRoom(@ApiParam(required = true) PackageRoomRequestBean pkgRoom){
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packageRoomService.createPackageRoom(pkgRoom);
            returnObj.put(serviceResponse.getStatus().toString(), serviceResponse.getMessage());
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        returnObj.put("packageRoom", pkgRoom);
        return Response.status(Response.Status.CREATED).entity(returnObj).build();
    }

    @ApiOperation(value = "删除套餐空间")
    @DELETE
    @Path("/room/{packageRoomId}")
    @LogAnnotation
    public Response removePackageRoom(@PathParam("packageRoomId") Long packageRoomId){
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packageRoomService.removePackageRoom(packageRoomId);
            if(!serviceResponse.getStatus()) {
                returnObj.put("failed", serviceResponse.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(returnObj).build();
            }
            returnObj.put("success", serviceResponse.getMessage());
        }  catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        returnObj.put("packageRoomId", packageRoomId);
        return Response.status(Response.Status.OK).entity(returnObj).build();
    }

    @ApiOperation(value = "创建套餐价格")
    @POST
    @Path("/price")
    @LogAnnotation
    public Response createPackagePrice(@ApiParam(required = true)PackagePriceRequestBean priceRequestBean) {
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packagePriceService.createPackagePrice(priceRequestBean);
            returnObj.put(serviceResponse.getStatus().toString(), serviceResponse.getMessage());

        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        returnObj.put("packagePrice", priceRequestBean);
        return Response.status(Response.Status.CREATED).entity(returnObj).build();
    }

    @ApiOperation(value = "删除套餐价格")
    @DELETE
    @Path("/price/{packagePriceId}")
    @LogAnnotation
    public Response removePackagePrice(@PathParam("packagePriceId") Long packagePriceId) {
        JSONObject returnObj =  new JSONObject();
        try {
            ServiceResponse serviceResponse = packagePriceService.removePackagePrice(packagePriceId);

            if(!serviceResponse.getStatus()) {
                returnObj.put("failed", serviceResponse.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(returnObj).build();
            }
            returnObj.put("success", serviceResponse.getMessage());
        }
        catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        return Response.status(Response.Status.OK).entity(returnObj).build();
    }

 	@ApiOperation(value = "创建主材")
    @POST
    @Path("/material")
    @LogAnnotation
    public Response createMaterial(@ApiParam(required = true) MaterialRequestBean materialRequestBean) {
        ServiceResponse serviceResponse;
        try {
            serviceResponse= materialService.createMaterial(materialRequestBean);
            if(!serviceResponse.getStatus()){
                return  Response.status(Response.Status.NOT_MODIFIED)
                        .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.CREATION_FAIL,serviceResponse.getMessage()))).build();
            }
        } catch (ParentException e) {
            logger.error(e.getErrorMsg());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        CommonRespBean commonRespBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, serviceResponse.getMessage()));
        return Response.status(Response.Status.CREATED).entity(commonRespBean).build();

    }
    @ApiOperation(value = "删除主材")
    @DELETE
    @Path("/material/{materialId}/brand/{brandCode}/product/{productCode}")
    @LogAnnotation
    public Response deleteMaterial(@ApiParam(required = true)
                                       @PathParam("materialId") long materialId,
                                       @PathParam("brandCode")String brandCode,
                                       @PathParam("productCode")String productCode
                                       ) {
        String[] productCodes = productCode.split(",");
        if(materialId==0||StringUtils.isBlank(brandCode)||productCodes.length<1){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new PackageException(PackageError.INSERT_PACKAGE_PARAMS_ERROR)).build();
        }
        ServiceResponse serviceResponse;
        try {
            List<String> pCodes = Arrays.asList(productCodes);
             serviceResponse = materialService.deleteMaterial(materialId,brandCode,pCodes);
            if(!serviceResponse.getStatus()){
                return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DELETING_FAIL,serviceResponse.getMessage()))).build();
            }
        } catch (ParentException e) {
            logger.error(e.getErrorMsg());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        return Response.status(Response.Status.OK)
                .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,serviceResponse.getMessage()))).build();
    }

    @ApiOperation(value = "获取门店信息")
    @GET
    @Path("/store")
    @LogAnnotation
    public Response getStores(@QueryParam("region")String region) {
        List<StorePo> stores = packageService.queryStores(region);
        return Response.status(Response.Status.OK).entity(stores).build();
    }
}
