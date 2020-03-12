package com.juran.quote.resource.v1;

import com.juran.core.exception.ParentException;
import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.dto.ServiceResponse;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.bean.response.PackageConstructResponseBean;
import com.juran.quote.bean.response.PageRespBean;
import com.juran.quote.config.QuoteConfig;
import com.juran.quote.exception.PackageException;
import com.juran.quote.service.PackageConstructService;
import com.juran.quote.utils.HttpUtils;
import com.juran.quote.utils.LogAnnotation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "施工项管理服务")
@RestLog
@Component
@Path("/v1/construct")
@Produces(MediaType.APPLICATION_JSON)
public class ConstructResource {
    @Autowired
    PackageConstructService packageConstructService;

    @Autowired
    QuoteConfig quoteConfig;

    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @POST
    @Path("/")
    @ApiOperation(value = "添加施工项")
    @LogAnnotation
    public Response createConstruct(ConstructBean source, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.insertConstruct(source);
            return Response.status(Response.Status.CREATED)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @PUT
    @Path("/")
    @ApiOperation(value = "编辑施工项")
    @LogAnnotation
    public Response updateConstruct(ConstructBean source, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.updateConstruct(source);
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
    @ApiOperation(value = "施工项列表查询 ")
    @LogAnnotation
    public Response getConstructs(@BeanParam QueryConstructBean source) {
        try {
            PageRespBean<ConstructBean> response = packageConstructService.getConstructList(source);
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


    @GET
    @Path("/code")
    @ApiOperation(value = "校验施工项编码是否存在")
    @LogAnnotation
    public Response getConstructs(@QueryParam("constructCode") String constructCode, @QueryParam("decorationCompany")String decorationCompany) {

        try {
            CommonRespBean serviceResponse = packageConstructService.checkConstructCode(decorationCompany, constructCode);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }

    @DELETE
    @Path("/{ids}")
    @ApiOperation(value = "删除施工项")
    @LogAnnotation
    public Response deleteConstruct(@PathParam("ids") String ids, @HeaderParam("User") String user) {
        String[] constructIds = ids.split(",");
        CommonRespBean serviceResponse;
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            List<String> idList = Arrays.asList(constructIds);
            List<Long> longIds = idList.parallelStream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
            serviceResponse = packageConstructService.deleteConstruct(longIds, userName);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("创建套餐施工项")
    @POST
    @Path("/packageConstruct")
    @LogAnnotation
    public Response createPackageConstruct(PackageConstructRequestBean requestBean) {
        try {
            CommonRespBean response = packageConstructService.createPackageConstruct(requestBean);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("查询套餐施工项")
    @GET
    @Path("/packageConstruct")
    @LogAnnotation
    public Response getPackageConstruct(@ApiParam("套餐版本ID") @QueryParam("packageVersionId") Long pkgVersionId,
                                        @ApiParam("施工项ID") @QueryParam("constructId") Long constructId,
                                        @QueryParam("offset") Integer offset,
                                        @QueryParam("limit") Integer limit) {
        try {
            PageRespBean<PackageConstructResponseBean> response =
                    packageConstructService.getPackageConstruct(pkgVersionId, constructId, offset, limit);
            PageRespBean respBean = new PageRespBean<>(response.getTotal(), response.getOffset(), response.getLimit());
            CommonRespBean commonRespBean =
                    new CommonRespBean(response.getData(), new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询套餐施工项成功"),
                            respBean);
            return Response.status(Response.Status.OK).entity(commonRespBean).build();
        } catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("删除套餐施工项")
    @DELETE
    @Path("/packageConstruct/{packageConstructIds}")
    @LogAnnotation
    public Response getPackageConstruct(@PathParam("packageConstructIds") String packageConstructIds) {

        try {
            CommonRespBean response = packageConstructService.removePackageConstruct(packageConstructIds);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (PackageException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("根据套餐版本id查询套餐施工项")
    @GET
    @Path("/packageConstruct/packageVersion/{pkgVersionId}")
    public Response getPackageConstructByPackageVersion(@PathParam("pkgVersionId") Long pkgVersionId) {
        ServiceResponse serviceResponse;
        try {
            serviceResponse = packageConstructService.getPackageConstructByPkgVersion(pkgVersionId);
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
        return Response.status(Response.Status.OK)
                .entity(serviceResponse.getData()).build();
    }

    @ApiOperation("查看套餐施工项的带出规则")
    @GET
    @Path("/packageConstruct/relationship/")
    @LogAnnotation
    public Response getSinglePackageConstruct(@ApiParam(required = true, value = "版本号") @QueryParam("version") Long version,
                                              @ApiParam(required = true, value = "施工项ID") @QueryParam("constructId") Long constructId,
                                              @ApiParam(required = false, value = "房型") @QueryParam("houseType") String houseType) {

        CommonRespBean<Map<String, Object>> serviceResponse = packageConstructService.getPkgConstructRelationship(constructId, version, houseType);
        return Response.status(Response.Status.OK).entity(serviceResponse.getData()).build();
    }


    @ApiOperation("创建礼包内施工项")
    @POST
    @Path("/bagConstruct")
    public Response createBagConstruct(BagConstructRequestBean requestBean) {
        try {
            ServiceResponse serviceResponse = packageConstructService.createBagConstruct(requestBean);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("批量创建施工项")
    @POST
    @Path("/batch")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response batchCreateConstructs(
            @FormDataParam("template") InputStream fileInputStream,
            @FormDataParam("template") FormDataContentDisposition file,
            @HeaderParam("User") String user,
            @FormDataParam("batchNumber") String batchNumber,
            @FormDataParam("decorationCompany") String decorationCompany) {
        if (file == null || StringUtils.isEmpty(batchNumber)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean serviceResponse =
                    packageConstructService.batchCreateConstruct(fileInputStream,
                            file,
                            userName,
                            batchNumber,
                            decorationCompany);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("查询批量创建施工项结果")
    @GET
    @Path("/batch")
    public Response getResultOfBatchConstructs(@ApiParam(required = false, value = "批次号") @QueryParam("batchNum") String batchNum,
                                               @ApiParam(required = false, value = "上传日期") @QueryParam("uploadDate") Long uploadDate,
                                               @ApiParam(required = false, value = "用户名") @QueryParam("user") String user,
                                               @ApiParam(required = true, value = "装饰公司") @QueryParam("decorationCompany") String decorationCompany,
                                               @ApiParam(required = false, value = "长传结果 1成功， 0失败") @QueryParam("uploadResult") Integer uploadResult,
                                               @QueryParam("offset") @DefaultValue("1") Integer offset,
                                               @QueryParam("limit") @DefaultValue("15") Integer limit) {
        try {
            CommonRespBean serviceResponse = packageConstructService.getBatchUploadResult(batchNum,
                    uploadDate,
                    user,
                    uploadResult,
                    decorationCompany,
                    offset,
                    limit);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("删除批量创建施工项结果")
    @DELETE
    @Path("/batch")
    public Response removeResultOfBatchConstructs(@ApiParam(required = true, value = "批量插入记录id") @QueryParam("batchResultIds") String batchResultIds) {
        if (StringUtils.isEmpty(batchResultIds)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            CommonRespBean serviceResponse = packageConstructService.removeBatchUploadResult(batchResultIds);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("下载批处理模板")
    @GET
    @Path("/batch/template/download")
    public Response downloadBatchTemplate() {
        try {
            CommonRespBean serviceResponse = packageConstructService.downloadConstructTemplate();
            return Response.ok(serviceResponse.getData(), MediaType.APPLICATION_OCTET_STREAM).header("content-disposition", "attachment; filename = template.xlsx").build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("验证批处理号")
    @GET
    @Path("/batch/number")
    public Response checkBatchNumber(@ApiParam(required = true, value = "批处理号")
                                     @QueryParam("batchNum") String batchNum) {
        if (StringUtils.isEmpty(batchNum)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要传入批处理号")).build();
        }
        try {
            CommonRespBean serviceResponse = packageConstructService.verifyBatchNumber(batchNum);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @PUT
    @Path("/batch")
    @ApiOperation(value = "批处理结果编辑单个施工项")
    @LogAnnotation
    public Response updateConstructInBatch(BatchUploadConstructRequest source, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.updateConstructInBatch(source);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


    @ApiOperation("导出所有施工项-excel")
    @GET
    @Path("/export")
    @LogAnnotation
    public Response exportConstruct(@Context ContainerRequestContext context, @BeanParam QueryExportConstructbean query) {

        String fileName = "施工项.xlsx";
        try {
            fileName = HttpUtils.encodeFileNameByAgent(context, fileName);
            CommonRespBean serviceResponse = packageConstructService.exportConstruct(query);
            return Response.ok(serviceResponse.getData(), MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment;filename=" + fileName).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        } catch (Exception e1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/price")
    @ApiOperation(value = "施工项价格列表查询 ")
    @LogAnnotation
    public Response getConstructPrices(@BeanParam QueryConstructPriceBean source) {
        try {
            PageRespBean<ConstructPriceBean> response = packageConstructService.getConstructPriceList(source);
            CommonRespBean commonRespBean = new CommonRespBean(response.getData(), new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询套餐施工项价格列表成功"), response);
            return Response.status(Response.Status.OK).entity(commonRespBean).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()))).build();
        }
    }

    @POST
    @Path("/price")
    @ApiOperation(value = "添加施工项价格")
    @LogAnnotation
    public Response createConstructPrice(ConstructPriceBean source, @HeaderParam("User") String user) {

        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.insertConstructPrice(source);
            return Response.status(Response.Status.CREATED)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @DELETE
    @Path("/price/{ids}")
    @ApiOperation(value = "删除施工项价格")
    @LogAnnotation
    public Response deleteConstructPrice(@PathParam("ids") String ids, @HeaderParam("User") String user) {
        String[] constructIds = ids.split(",");
        CommonRespBean serviceResponse;
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            List<String> idList = Arrays.asList(constructIds);
            List<Long> longIds = idList.parallelStream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
            serviceResponse = packageConstructService.deleteConstructPrices(longIds, userName);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @GET
    @Path("/price/check")
    @ApiOperation(value = "校验施工项价格是否可创建")
    @LogAnnotation
    public Response checkConstructPrice(@ApiParam(value = "quoteVersionId", required = true) @QueryParam("quoteVersionId") Long quoteVersionId,
                                        @ApiParam(value = "quoteTypeId", required = true) @QueryParam("quoteTypeId") Long quoteTypeId,
                                        @ApiParam(value = "constructCode", required = true) @QueryParam("constructCode") String constructCode,
                                        @ApiParam(value = "decorationTypeId", required = true) @QueryParam("decorationTypeId") Long decorationTypeId) {
        try {
            ConstructPriceBean requestBean = new ConstructPriceBean();
            requestBean.setQuoteTypeId(quoteTypeId);
            requestBean.setQuoteVersionId(quoteVersionId);
            requestBean.setDecorationTypeId(decorationTypeId);
            requestBean.setConstructCode(constructCode);
            CommonRespBean serviceResponse = packageConstructService.checkConstructPrice(requestBean);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg())))
                    .build();
        }
    }


    @PUT
    @Path("/price")
    @ApiOperation(value = "编辑施工项价格")
    @LogAnnotation
    public Response updateConstruct(ConstructPriceBean source, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.updateConstructPrice(source);
            return Response.status(Response.Status.OK)
                    .entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }

    @ApiOperation("批量创建施工项价格")
    @POST
    @Path("/price/batch")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response batchCreateConstructPrices(
            @FormDataParam("template") InputStream fileInputStream,
            @FormDataParam("template") FormDataContentDisposition file,
            @HeaderParam("User") String user,
            @FormDataParam("batchNumber") String batchNumber,
            @FormDataParam("decorationCompany") String decorationCompany) {
        if (file == null || StringUtils.isEmpty(batchNumber) || StringUtils.isEmpty(decorationCompany)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        if (StringUtils.isEmpty(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
        }
        try {
            String userName = HttpUtils.decodeUser(user);
            CommonRespBean serviceResponse = packageConstructService.batchCreateConstructPrice(fileInputStream, file, userName, batchNumber,decorationCompany);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }


    @ApiOperation("条件查询批量创建施工项价格结果")
    @GET
    @Path("/price/batch")
    public Response getResultOfBatchConstructPrice(@ApiParam("批次号") @QueryParam("batchNum") String batchNum,
                                                   @ApiParam("上传日期") @QueryParam("uploadDate") Long uploadDate,
                                                   @ApiParam("用户名") @QueryParam("user") String user,
                                                   @ApiParam("上传结果 1成功， 0失败") @QueryParam("uploadResult") Integer uploadResult,
                                                   @QueryParam("offset") @DefaultValue("1") Integer offset,
                                                   @QueryParam("limit") @DefaultValue("15") Integer limit,
                                                   @ApiParam("装饰公司") @QueryParam("decorationCompany") String decorationCompany) {
        try {
            CommonRespBean serviceResponse = packageConstructService.getBatchUploadConstructPriceResult(batchNum, uploadDate, user, uploadResult, offset, limit,decorationCompany);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("删除批量创建施工项价格结果")
    @DELETE
    @Path("/price/batch")
    public Response removeResultOfBatchConstructPrice(@ApiParam(required = true, value = "批量插入记录id") @QueryParam("batchResultIds") String batchResultIds) {
        if (StringUtils.isEmpty(batchResultIds)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误")).build();
        }
        try {
            CommonRespBean serviceResponse = packageConstructService.removeBatchUploadConstructPriceResult(batchResultIds);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("下载施工项价格模板")
    @GET
    @Path("/price/batch/template/download")
    public Response downloadConstructPriceTemplate() {
        try {
            CommonRespBean serviceResponse = packageConstructService.downloadConstructPriceTemplate();
            return Response.ok(serviceResponse.getData(), MediaType.APPLICATION_OCTET_STREAM).header("content-disposition", "attachment; filename = template.xlsx").build();
        } catch (PackageException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @ApiOperation("验证施工项价格批次号")
    @GET
    @Path("/price/batch/number")
    public Response checkConstructPriceBatchNumber(@ApiParam(required = true, value = "批处理号")
                                                   @QueryParam("batchNum") String batchNum) {
        if (StringUtils.isEmpty(batchNum)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要传入批处理号")).build();
        }
        try {
            CommonRespBean serviceResponse = packageConstructService.checkConstructPriceBatchNum(batchNum);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toResponseJson()).build();
        }
    }

    @PUT
    @Path("/price/batch")
    @ApiOperation(value = "批处理结果编辑单个施工项价格")
    @LogAnnotation
    public Response updateConstructPriceInBatch(BatchUploadConstructPriceRequest source, @HeaderParam("User") String user) {
        try {
            if (StringUtils.isEmpty(user)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            String userName = HttpUtils.decodeUser(user);
            if (StringUtils.isEmpty(userName)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean<>("输入参数有误,需要用户信息")).build();
            }
            source.setUpdateBy(userName);
            CommonRespBean serviceResponse = packageConstructService.updateConstructPriceInBatch(source);
            return Response.status(Response.Status.OK).entity(serviceResponse).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respBean)
                    .build();
        }
    }


    @POST
    @Path("/formula")
    @ApiOperation(value = "创建施工项数量计算公式")
    @LogAnnotation
    public Response createConstructCalculationFormula(CalculationFormulaBean requestBean) {
        try {
            if (requestBean.getConstructId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("输入错误，施工项id为空")).build();
            }
            if (StringUtils.isEmpty(requestBean.getExpression())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("输入错误，施工项计算公式为空")).build();
            }
            CommonRespBean response = packageConstructService.createConstructCalculationFormula(requestBean);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @GET
    @Path("/formula/{constructId}")
    @ApiOperation(value = "查询施工项数量计算公式")
    @LogAnnotation
    public Response createConstructCalculationFormula(@PathParam("constructId") Long constructId) {
        try {
            CommonRespBean response = packageConstructService.getConstructCalculationFormula(constructId);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @PUT
    @Path("/formula")
    @ApiOperation(value = "编辑施工项数量计算公式")
    @LogAnnotation
    public Response updateConstructCalculationFormula(CalculationFormulaBean requestBean) {
        try {
            if (requestBean.getExpressionId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("输入错误，表达式id为空")).build();
            }
            if (StringUtils.isEmpty(requestBean.getExpression())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new CommonRespBean("输入错误，施工项计算公式为空")).build();
            }
            CommonRespBean response = packageConstructService.updateConstructCalculationFormula(requestBean);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

    @DELETE
    @Path("/formula/{constructId}")
    @ApiOperation(value = "删除施工项数量计算公式")
    @LogAnnotation
    public Response deleteConstructCalculationFormula(@PathParam("constructId") Long constructId) {
        try {
            CommonRespBean response = packageConstructService.deleteConstructCalculationFormula(constructId);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (ParentException e) {
            CommonRespBean respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(respBean).build();
        }
    }

}
