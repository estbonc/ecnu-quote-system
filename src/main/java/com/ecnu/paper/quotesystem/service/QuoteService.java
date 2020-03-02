package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecnu.paper.quotesystem.bean.PagingData;
import com.ecnu.paper.quotesystem.bean.dto.BindMaterial;
import com.ecnu.paper.quotesystem.bean.dto.BindingConstruct;
import com.ecnu.paper.quotesystem.bean.dto.ChargeTypeDto;
import com.ecnu.paper.quotesystem.bean.dto.DecorationMaterial;
import com.ecnu.paper.quotesystem.bean.dto.GetQuoteDetailDto;
import com.ecnu.paper.quotesystem.bean.dto.GetQuoteRoomDetailDto;
import com.ecnu.paper.quotesystem.bean.dto.GetQuoteSummaryDto;
import com.ecnu.paper.quotesystem.bean.dto.ProjectDecorationCompanyInfo;
import com.ecnu.paper.quotesystem.bean.dto.QuoteCustomerDto;
import com.ecnu.paper.quotesystem.bean.dto.QuoteDetailDto;
import com.ecnu.paper.quotesystem.bean.dto.QuoteResult;
import com.ecnu.paper.quotesystem.bean.dto.QuoteRoomDto;
import com.ecnu.paper.quotesystem.bean.enums.ChargeCalculationTypeEnums;
import com.ecnu.paper.quotesystem.bean.enums.ChargeRateBaseEnums;
import com.ecnu.paper.quotesystem.bean.enums.ExportExcelEnums;
import com.ecnu.paper.quotesystem.bean.enums.QuoteErrorEnum;
import com.ecnu.paper.quotesystem.bean.enums.QuoteHouseEnum;
import com.ecnu.paper.quotesystem.bean.enums.QuoteItemTypeEnum;
import com.ecnu.paper.quotesystem.bean.enums.QuoteRoomEnum;
import com.ecnu.paper.quotesystem.bean.exception.QuoteException;
import com.ecnu.paper.quotesystem.bean.mini.MiniProductRespBean;
import com.ecnu.paper.quotesystem.bean.po.ConstructPo;
import com.ecnu.paper.quotesystem.bean.po.ConstructPricePo;
import com.ecnu.paper.quotesystem.bean.po.ConstructRelationship;
import com.ecnu.paper.quotesystem.bean.po.HouseTypePo;
import com.ecnu.paper.quotesystem.bean.po.PackageBag;
import com.ecnu.paper.quotesystem.bean.po.PackagePrice;
import com.ecnu.paper.quotesystem.bean.po.Product;
import com.ecnu.paper.quotesystem.bean.po.QuoteDocumentPo;
import com.ecnu.paper.quotesystem.bean.po.QuotePo;
import com.ecnu.paper.quotesystem.bean.po.QuoteRoomConstructPo;
import com.ecnu.paper.quotesystem.bean.po.QuoteRoomMaterialBrandPo;
import com.ecnu.paper.quotesystem.bean.po.QuoteRoomMaterialPo;
import com.ecnu.paper.quotesystem.bean.po.QuoteRoomPo;
import com.ecnu.paper.quotesystem.bean.po.QuoteRoomTextilePo;
import com.ecnu.paper.quotesystem.bean.po.QuoteVersionPo;
import com.ecnu.paper.quotesystem.bean.po.RoomPo;
import com.ecnu.paper.quotesystem.bean.quote.BomRoomDto;
import com.ecnu.paper.quotesystem.bean.request.ConstCalcRequest;
import com.ecnu.paper.quotesystem.bean.request.CreateQuoteRequestBean;
import com.ecnu.paper.quotesystem.bean.request.PutQuoteBaseInfoReqBean;
import com.ecnu.paper.quotesystem.bean.response.CommonRespBean;
import com.ecnu.paper.quotesystem.bean.response.ConstCalcResponse;
import com.ecnu.paper.quotesystem.bean.response.GetQuoteBaseInfoRespBean;
import com.ecnu.paper.quotesystem.bean.response.GetQuoteSummaryPriceRespBean;
import com.ecnu.paper.quotesystem.bean.response.HttpResponseList;
import com.ecnu.paper.quotesystem.bean.response.PackageInfoRespBean;
import com.ecnu.paper.quotesystem.bean.response.QuerySupportConstructRespBean;
import com.ecnu.paper.quotesystem.bean.response.QuoteBaseInfoRespBean;
import com.ecnu.paper.quotesystem.bean.response.QuoteShareUrlRespBean;
import com.ecnu.paper.quotesystem.bean.response.TextilesRespBean;
import com.ecnu.paper.quotesystem.config.CalculatorVarBean;
import com.ecnu.paper.quotesystem.config.Constants;
import com.ecnu.paper.quotesystem.config.QuoteConfig;
import com.ecnu.paper.quotesystem.config.TextileThirdUrlConfig;
import com.ecnu.paper.quotesystem.exception.PackageException;
import com.ecnu.paper.quotesystem.service.executorTasks.QuoteProcessor;
import com.ecnu.paper.quotesystem.utils.CalObjectValueBean;
import com.ecnu.paper.quotesystem.utils.ClassUtil;
import com.ecnu.paper.quotesystem.utils.CommonStaticConst;
import com.ecnu.paper.quotesystem.utils.DateUtils;
import com.ecnu.paper.quotesystem.utils.JsonUtility;
import com.ecnu.paper.quotesystem.utils.KeyManagerUtil;
import com.ecnu.paper.quotesystem.utils.LogAnnotation;
import com.ecnu.paper.quotesystem.utils.MDQueryUtil;
import com.ecnu.paper.quotesystem.utils.QuoteUtils;
import com.ecnu.paper.quotesystem.utils.RedisLock;
import com.ecnu.paper.quotesystem.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/6/27 16:56
 * @description
 */
@Service
public class QuoteService extends BaseService {

    @Autowired
    private QuoteConfig quoteConfig;
    @Autowired
    private TextileThirdUrlConfig textileThirdUrlConfig;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PackageRoomService packageRoomService;
    @Autowired
    private KeyManagerUtil keyManagerUtil;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private ConstructService constructService;
    @Autowired
    D3CaseFeignClient d3CaseFeignClient;
    @Autowired
    CatentryFeignClient catentryFeignClient;
    @Autowired
    IPriceFeignClient priceFeignClient;
    @Autowired
    TaoBaoService taoBaoService;

    @Autowired
    QuoteProcessor quoteProcessor;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    ExcelService excelService;


    public Long createQuote(CreateQuoteRequestBean bom) throws QuoteException {

        String message;
        Properties properties = new Properties();
        properties.put("designId", bom.getDesignID());
        QuoteDocumentPo quotePo = findPoByUniqueKey(properties, QuoteDocumentPo.class);
        //报价无历史，如果查出来报价则更新，没有查出来则新建，返回报价ID
        if (quotePo != null) {
            quotePo.setProjectId(bom.getAcsProjectID());
            quotePo.setHouseTypeCode(bom.getRoomNum());
            quotePo.setUpdateTime(new Date());
            buildDesignRoomListForQuote(bom, quotePo);
            WriteResult writeResult = updateQuote(quotePo, Optional.of("designId"), Optional.of(bom.getDesignID()));
            if (writeResult.getN() == 0) {
                message = String.format("开始报价-更新报价失败,designId:%s.", bom.getDesignID());
                logger.info(message);
                throw new QuoteException(message);
            } else {
                logger.info("开始报价-更新报价成功,designId:{}.", bom.getDesignID());
            }
        } else {
            quotePo = new QuoteDocumentPo();
            quotePo.setStep(1);
            quotePo.setQuoteId(keyManagerUtil.get16PlacesId());
            quotePo.setDesignId(bom.getDesignID());
            quotePo.setProjectId(bom.getAcsProjectID());
            quotePo.setCreateTime(new Date());
            quotePo.setHouseTypeCode(bom.getRoomNum());
            quotePo.setDecorationCompany(bom.getDecorationCompany());
            buildDesignRoomListForQuote(bom, quotePo);
            mongoTemplate.insert(quotePo);
        }
        quoteProcessor.buildQuoteRooms(bom, quotePo);
        return quotePo.getQuoteId();
    }

    private void buildDesignRoomListForQuote(CreateQuoteRequestBean bom, QuoteDocumentPo quotePo) {
        Properties roomTypeSelector = new Properties();
        List<BomRoomDto> rooms = bom.getRoomList().stream().map(room -> {
            BomRoomDto bomRoom = new BomRoomDto();
            roomTypeSelector.clear();
            roomTypeSelector.put(propertiesUtils.getProperty("dictionary.englishName"), room.getRoomType());
            try {
                RoomPo roomPo = findPoByUniqueKey(roomTypeSelector, RoomPo.class);
                if (roomPo == null) {
                    logger.error("获取房间主数据失败{}", room.getRoomType());
                    bomRoom.setRoomType("");
                } else {
                    bomRoom.setRoomType(roomPo.getRoomName());
                }
            } catch (QuoteException e) {
                logger.error("获取房间主数据出错{}", e.getErrorMsg());
                bomRoom.setRoomType("");
            }
            bomRoom.setRoomName(room.getRoomName());
            BigDecimal area = BigDecimal.valueOf(Double.valueOf(room.getArea())).setScale(2, BigDecimal.ROUND_HALF_UP);
            bomRoom.setArea(area.toPlainString());
            return bomRoom;
        }).collect(Collectors.toList());
        quotePo.setDesignRooms(rooms);
    }

    /**
     * 验证设计方案是否符合报价规则
     * 1.验证案例户型是否被支持
     * 2.验证设计方案中是否包含没有设置房间类型的房间
     *
     * @param bom 3D 带出bom
     * @return
     * @throws QuoteException
     */
    public CommonRespBean verifyDesignCase(CreateQuoteRequestBean bom) throws QuoteException {
        if (StringUtils.isEmpty(bom.getRoomNum())) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING,
                    "装修设计未设置户型，请设置户型后继续!"));
        }

        Properties properties = propertiesUtils
                .buildProperties(new String[]{propertiesUtils.getProperty("houseType.code"), propertiesUtils.getProperty("common.status")},
                        new Object[]{bom.getRoomNum(), CommonStaticConst.Common.ENABLE_STATUS});
        HouseTypePo houseTypePo = findPoByUniqueKey(properties, HouseTypePo.class);
        if (houseTypePo == null) {
            String houseType = StringUtil.parseHouseTypeCode(bom.getRoomNum());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING,
                    String.format("暂不支持户型%s, 请联系装饰公司管理员录入户型后继续！", houseType)));
        }

        Optional<BomRoomDto> roomWithoutType = bom.getRoomList()
                .stream()
                .filter(roomBom -> StringUtils.isEmpty(roomBom.getRoomType()))
                .findFirst();

        if (roomWithoutType.isPresent()) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING,
                    "存在没有设置房间类型的房间，请为房间设置房间类型后继续！"));
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS,
                "验证通过"));

    }

    /**
     * 获取报价基本信息baseInfo
     *
     * @param quoteId 报价id
     * @return
     * @throws QuoteException
     */
    @LogAnnotation
    public CommonRespBean getQuoteBaseInfo(Long quoteId) throws QuoteException {
        if (redisLock.acquireAndRelease(quoteId.toString())) {
            QuoteDocumentPo quotePo = getQuotePoById(quoteId);
            QuoteBaseInfoRespBean respBean = doGetQuoteBaseInfo(quotePo);
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取报价基本信息成功"), respBean);
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, "获取报价基本信息失败"));
    }

    private QuoteBaseInfoRespBean doGetQuoteBaseInfo(QuoteDocumentPo quote) throws QuoteException {
        QuoteBaseInfoRespBean result = new QuoteBaseInfoRespBean();
        //获取装饰公司信息
        if (StringUtils.isEmpty(quote.getDecorationCompany())) {
            String decorationCompany = getDecorationCompanyFromConstructionPlatform(quote.getDesignId());
            result.setDecorationCompany(StringUtils.isEmpty(decorationCompany) ?
                    "居然装饰" :
                    decorationCompany);
        } else {
            result.setDecorationCompany(quote.getDecorationCompany());
        }
        //获取户型信息
        result.setHouseType(quote.getHouseTypeCode());
        Properties properties = new Properties();

        properties.put(propertiesUtils.getProperty("houseType.code"), quote.getHouseTypeCode());
        properties.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
        if (!StringUtils.isEmpty(quote.getDecorationCompany())) {
            properties.put(propertiesUtils.getProperty("common.decorationCompany"), quote.getDecorationCompany());
        }
        HouseTypePo houseTypePo = findPoByUniqueKey(properties, HouseTypePo.class);
        if (houseTypePo != null) {
            result.setHouseTypeName(houseTypePo.getHouseType());
            result.setHouseTypeId(houseTypePo.getHouseTypeId());
        }
        //获取客户基本信息
        D3CaseBaseInfoRespBean caeBaseInfo = d3CaseFeignClient.getCaeBaseInfo(quote.getDesignId());
        if (caeBaseInfo == null) {
            logger.info("[QuoteService:doGetQuoteBaseInfo]获取案例信息失败{}", quote.getDesignId());
        }
        if (quote.getCustomerInfo() != null) {
            QuoteCustomerDto customerInfo = quote.getCustomerInfo();
            result.setCustomerName(customerInfo.getCustomerName());
            result.setCustomerMobile(customerInfo.getCustomerMobile());
            result.setProvinceId(customerInfo.getProvinceId());
            result.setCityId(customerInfo.getCityId());
            result.setDistrictId(customerInfo.getDistrictId());
            result.setProvince(customerInfo.getProvince());
            result.setCity(customerInfo.getCity());
            result.setDistrict(customerInfo.getDistrict());
            result.setCommunityName(customerInfo.getCommunityName());
        } else if (caeBaseInfo != null) {
            result.setCustomerName(caeBaseInfo.getCustomerName());
            result.setCustomerMobile(caeBaseInfo.getCustomerMobile());
            result.setProvinceId(caeBaseInfo.getProvinceId());
            result.setCityId(caeBaseInfo.getCityId());
            result.setDistrictId(caeBaseInfo.getDistrictId());
            result.setProvince(caeBaseInfo.getProvince());
            result.setCity(caeBaseInfo.getCity());
            result.setDistrict(caeBaseInfo.getDistrict());
            result.setCommunityName(caeBaseInfo.getCommunityName());
        }
        if (quote.getDesignerName() != null) {
            result.setMemberName(quote.getDesignerName());
        } else if (caeBaseInfo != null) {
            result.setMemberName(caeBaseInfo.getMemberName());
        }

        //获取房间信息
        result.setRoomBaseInfo(quote.getDesignRooms());
        if (quote.getInnerArea() != null) {
            result.setInnerArea(quote.getInnerArea());
        }
        //获取报价规则信息
        result.setIsBindProject(quote.getProjectId() == null || quote.getProjectId().equals(0) ? 0 : 1);
        result.setQuoteType(quote.getQuoteType());
        result.setQuoteTypeId(quote.getQuoteTypeId());
        result.setDecorationType(quote.getDecorationType());
        result.setDecorationTypeId(quote.getDecorationTypeId());

        return result;
    }

    /**
     * 更新报价-客户房屋信息
     *
     * @param reqBean
     * @throws QuoteException
     */
    @LogAnnotation
    public CommonRespBean updateQuoteBaseInfo(PutQuoteBaseInfoReqBean reqBean, String tokenValue) throws QuoteException {
        //更新报价基本信息
        QuoteDocumentPo quotePo = getQuotePoById(reqBean.getQuoteId());
        Properties versionInfo = getQuoteVersion(reqBean.getDecorationTypeId(), reqBean.getProvinceId());
        if (versionInfo != null) {
            quotePo.setQuoteVersionId((Long) versionInfo.get(propertiesUtils.getProperty("quoteVersion.quoteVersionId")));
            quotePo.setQuoteVersionCode((String) versionInfo.get(propertiesUtils.getProperty("quoteVersion.quoteVersionCode")));
        }
        quotePo.setInnerArea(reqBean.getInnerArea());
        quotePo.setDecorationType(reqBean.getDecorationType());
        quotePo.setDecorationTypeId(reqBean.getDecorationTypeId());
        quotePo.setQuoteTypeId(reqBean.getQuoteTypeId());
        quotePo.setQuoteType(reqBean.getQuoteType());
        HouseTypePo houseTypeById = packageRoomService.getHouseTypeById(reqBean.getHouseTypeId());
        if (houseTypeById == null) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "无对应的户型"));
        }
        quotePo.setHouseTypeId(houseTypeById.getHouseTypeId());
        quotePo.setHouseType(houseTypeById.getHouseType());
        quotePo.setHouseTypeCode(houseTypeById.getHouseTypeCode());
        quotePo.setDecorationCompany(reqBean.getDecorationCompany());
        //更新客户基本信息
        QuoteCustomerDto customerInfo = quotePo.getCustomerInfo();
        if (customerInfo == null) {
            customerInfo = new QuoteCustomerDto();
        }
        customerInfo.setCustomerName(reqBean.getCustomerName());
        customerInfo.setCustomerMobile(reqBean.getCustomerMobile());
        customerInfo.setProvince(reqBean.getProvince());
        customerInfo.setProvinceId(reqBean.getProvinceId());
        customerInfo.setCity(reqBean.getCity());
        customerInfo.setCityId(reqBean.getCityId());
        customerInfo.setDistrict(reqBean.getDistrict());
        customerInfo.setDistrictId(reqBean.getDistrictId());
        customerInfo.setCommunityName(reqBean.getCommunityName());
        quotePo.setCustomerInfo(customerInfo);
        quotePo.setDesignerName(reqBean.getDesignerName());
        quotePo.setStep(2);

        try {
            WriteResult writeResult = updateQuote(quotePo, Optional.of("quoteId"), Optional.of(reqBean.getQuoteId()));
            if (writeResult.getN() == 0) {
                logger.info("更新报价基本信息失败,quoteId:{}.", reqBean.getQuoteId());
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, "更新报价基本信息失败"));
            } else {
                logger.info("更新报价基本信息成功,quoteId:{}.", reqBean.getQuoteId());
            }
        } catch (Exception e) {
            throw new QuoteException("更新报价基本信息出错");
        }
        //更新案例信息
        if (reqBean.getIsBindProject() == 0) {
            D3CasesBaseInfoUpdateReqBean d3CasesBaseInfoUpdateReqBean = new D3CasesBaseInfoUpdateReqBean();
            BeanUtils.copyProperties(reqBean, d3CasesBaseInfoUpdateReqBean);
            d3CasesBaseInfoUpdateReqBean.setRoomType(reqBean.getHouseType());
            d3CasesBaseInfoUpdateReqBean.setArea(reqBean.getInnerArea().doubleValue());
            d3CasesBaseInfoUpdateReqBean.setHsDesignId(quotePo.getDesignId());
            d3CasesBaseInfoUpdateReqBean.setSjjSessionId(tokenValue);
            logger.info("quoteId:{}更新客户信息,传参:{}", reqBean.getQuoteId(), JsonUtil.toJson(d3CasesBaseInfoUpdateReqBean));
            try {
                JSONObject updateCaseBaseInfoResult = d3CaseFeignClient.updateCaeBaseInfo(d3CasesBaseInfoUpdateReqBean);
                if (updateCaseBaseInfoResult != null) {
                    logger.info("quoteId:{}更新案例客户信息,结果:{}", reqBean.getQuoteId(), updateCaseBaseInfoResult.toJSONString());
                } else {
                    logger.info("quoteId:{}更新案例客户信息错误,quoteId:{}", reqBean.getQuoteId());
                }
            } catch (Exception e) {
                throw new QuoteException("更新案例客户信息异常");
            }
        }

        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "更新报价基本信息成功"));
    }


    /**
     * 根据quoteId查询quotePo
     *
     * @param quoteId 报价id
     * @return
     * @throws QuoteException
     */
    @LogAnnotation
    public QuoteDocumentPo getQuotePoById(Long quoteId) throws QuoteException {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put("quoteId", quoteId);

        Query basicQuery = MDQueryUtil.getDBObjectByValuesAndLike(valuesMap, Lists.newArrayList());
        QuoteDocumentPo quotePo = mongoTemplate.findOne(basicQuery, QuoteDocumentPo.class);
        if (quotePo == null) {
            throw new QuoteException(QuoteErrorEnum.QUOTE_NOT_FOUND);
        }
        return quotePo;
    }

    /**
     * 获取报价详细信息
     *
     * @param quoteId 报价id
     * @return
     * @throws QuoteException
     */
    @LogAnnotation
    public CommonRespBean getQuoteDetailDto(Long quoteId) throws QuoteException {
        if (redisLock.acquireAndRelease(quoteId.toString())) {
            QuoteDocumentPo quote = getQuotePoById(quoteId);
            QuoteBaseInfoRespBean quoteBaseInfo = doGetQuoteBaseInfo(quote);
            GetQuoteDetailDto result = new GetQuoteDetailDto();
            BeanUtils.copyProperties(quoteBaseInfo, result);
            List<QuoteRoomDto> rooms = getQuoteRoomDetailDto(quote);
            result.setRooms(rooms);
            result.setExtraCharges(quote.getExtraCharges());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取报价详情成功"), result);
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, "获取报价详情失败"));
    }

    /**
     * 获取每个房间的装修材料和施工项详细信息, 如果当前是第一次报价，此方法会计算空间和主材自动带出的施工项。
     * 如果当前是在更新报价，此方法会保留之前手动添加的施工项数据，然后重新计算自动带出的施工项。
     *
     * @param quote 报价对象
     * @return
     */
    private List<QuoteRoomDto> getQuoteRoomDetailDto(QuoteDocumentPo quote) throws QuoteException {
        //构建施工项数量计算变量值对象
        CalculatorVarBean varValueBean = buildCalcuationVarValue(quote);
        List<QuoteRoomDto> rooms = quote.getRooms();
        try {
            for (QuoteRoomDto room : rooms) {
                varValueBean.setRoomArea(room.getArea());
                varValueBean.setRoomPerimeter(room.getPerimeter());
                varValueBean.setRoomHeight(room.getHeight());
                getConstructsForRoom(quote.getDecorationCompany(),
                        quote.getQuoteVersionId(),
                        quote.getDecorationTypeId(),
                        quote.getQuoteTypeId(), room, varValueBean, quote.getHouseTypeId());
                getConstructsForMaterial(quote.getDecorationCompany(),
                        quote.getQuoteTypeId(),
                        quote.getQuoteVersionId(),
                        quote.getDecorationTypeId(), room, varValueBean, quote.getHouseTypeId());
            }
        } catch (Exception e) {
            logger.error("获取报价房间详细信息错误{}", e.getMessage());
            throw new QuoteException("获取报价房间详细信息错误");
        }
        return rooms;
    }

    private void getConstructsForMaterial(String decorationCompany,
                                          Long quoteTypeId,
                                          Long quoteVersionId,
                                          Long decorationTypeId,
                                          QuoteRoomDto room,
                                          CalculatorVarBean varValueBean,
                                          Long houseTypeId) {
        //获取所有主材能带出的施工项集合
        List<DecorationMaterial> hardDecorationMaterials = room.getHardDecorationMaterials();
        List<String> materialCategoryIds = hardDecorationMaterials.stream().map(m -> m.getCategoryId()).collect(Collectors.toList());
        List<ConstructRelationship> allConstructs = Lists.newArrayList();

        BasicDBObject basicDBObject = new BasicDBObject();
        //houseTypeId为null的是查询全部户型的施工项带出关系
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), decorationCompany);
        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quoteTypeId);
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), new BasicDBObject("$eq", null));
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindMaterials.categoryId"), new BasicDBObject("$in", materialCategoryIds));
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindMaterials.isDefault"), CommonStaticConst.Construct.DEFAULT);
        List<ConstructRelationship> allMaterialConstructs = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructRelationship.class);
        if (!CollectionUtils.isEmpty(allMaterialConstructs)) {
            allConstructs.addAll(allMaterialConstructs);
        }
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), houseTypeId);
        List<ConstructRelationship> exactMaterialConstructs = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructRelationship.class);
        if (!CollectionUtils.isEmpty(exactMaterialConstructs)) {
            allConstructs.addAll(exactMaterialConstructs);
        }
        if (CollectionUtils.isEmpty(allConstructs)) {
            logger.info("没有要带出的主材相关的施工项");
            return;
        }
        //首先把主材相关的施工项数量都清零
        List<Long> allMaterialConstructIds = allConstructs.stream().map(c -> c.getConstructId()).collect(Collectors.toList());
        room.getConstructList().stream().forEach(con -> {
            if (allMaterialConstructIds.contains(con.getConstructId())) {
                con.setUsedQuantity(BigDecimal.ZERO);
            }
        });

        //循环主材列表，对每个主材带出的施工项进行操作
        for (DecorationMaterial material : hardDecorationMaterials) {
            varValueBean.setMaterialQuantity(material.getUsedQuantity());
            //获取当前主材绑定的施工项列表
            List<ConstructRelationship> materialConstructs = allConstructs.stream().filter(cr -> {
                BindMaterial bindMaterial = new BindMaterial();
                bindMaterial.setMaterialCategoryId(material.getCategoryId());

                return cr.getBindMaterials().indexOf(bindMaterial) > -1;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(materialConstructs)) {
                continue;
            }
            //循环主材带出的施工项执行更新数量或者保存新施工项。
            materialConstructs.stream().forEach(mc -> {
                BindingConstruct bindingConstruct = new BindingConstruct();
                bindingConstruct.setConstructId(mc.getConstructId());
                //如果改房间施工项列表已存在带出施工项，只执行更新数量
                if (room.getConstructList().contains(bindingConstruct)) {
                    try {
                        bindingConstruct = room.getConstructList().get(room.getConstructList().indexOf(bindingConstruct));
                        BigDecimal constructQuantity = constructService.calConstruct(mc.getConstructId(),
                                new CalObjectValueBean(varValueBean));
                        BigDecimal usedQuantity = bindingConstruct.getUsedQuantity().add(constructQuantity).setScale(2, BigDecimal.ROUND_HALF_UP);
                        bindingConstruct.setUsedQuantity(usedQuantity);
                        return;
                    } catch (PackageException e) {
                        logger.error("计算施工项:{}数量错误:{}", mc.getConstructCode(), e.getMessage());
                    }
                } else {
                    //如果不存在带出施工项, 执行自动带出施工项并计算数量
                    ConstructPo construct = cacheUtil.getCachedIndiviConstruct(mc.getConstructCode());
                    if (construct != null) {
                        bindingConstruct.setConstructCode(construct.getConstructCode());
                        bindingConstruct.setConstructId(construct.getConstructId());
                        bindingConstruct.setConstructName(construct.getConstructName());
                        bindingConstruct.setDesc(construct.getDesc());
                        bindingConstruct.setAssitSpec(construct.getAssitSpec());
                        bindingConstruct.setUnitCode(construct.getUnitCode());
                    } else {
                        logger.error("施工项自动带出-获取缓存施工项失败  constructCode:{}",mc.getConstructCode());
                        return;
                    }
                    // 获取施工项价格
                    Properties properties = propertiesUtils.buildProperties(new String[]{
                                    propertiesUtils.getProperty("quoteVersion.quoteVersionId"),
                                    propertiesUtils.getProperty("quoteType.quoteTypeId"),
                                    propertiesUtils.getProperty("decorationType.decorationTypeId"),
                                    propertiesUtils.getProperty("construct.constructCode"),
                                    propertiesUtils.getProperty("common.decorationCompany"),
                                    propertiesUtils.getProperty("common.status")
                            },
                            new Object[]{
                                    quoteVersionId==null?0L:quoteVersionId,
                                    quoteTypeId,
                                    decorationTypeId,
                                    construct.getConstructCode(),
                                    decorationCompany,
                                    CommonStaticConst.Common.ENABLE_STATUS
                            });
                    try {
                        ConstructPricePo constructPrice = findPoByUniqueKey(properties, ConstructPricePo.class);
                        if (constructPrice == null) {
                            logger.error("获取施工项:{}价格不存在", construct.getConstructCode());
                            bindingConstruct.setUnitPrice(BigDecimal.ZERO);
                            bindingConstruct.setCustomerPrice(BigDecimal.ZERO);
                            bindingConstruct.setForemanPrice(BigDecimal.ZERO);
                        } else {
                            bindingConstruct.setUnitPrice(constructPrice.getCustomerPrice());
                            bindingConstruct.setCustomerPrice(constructPrice.getCustomerPrice());
                            bindingConstruct.setForemanPrice(constructPrice.getForemanPrice());
                        }
                    } catch (QuoteException e) {
                        logger.error("获取施工项:{}价格错误:{}", construct.getConstructCode(), e.getMessage());
                    }

                    //计算施工项施工项使用量
                    try {
                        BigDecimal constructQuantity = constructService.calConstruct(construct.getConstructId(),
                                new CalObjectValueBean(varValueBean));
                        bindingConstruct.setUsedQuantity(constructQuantity);
                    } catch (PackageException e) {
                        logger.error("计算施工项:{}数量错误:{}", construct.getConstructCode(), e.getMessage());
                    }
                    room.getConstructList().add(bindingConstruct);
                }
            });
        }
    }

    private CalculatorVarBean buildCalcuationVarValue(QuoteDocumentPo quote) {
        CalculatorVarBean bean = new CalculatorVarBean();
        bean.setNumOfTotalRooms(quote.getRooms().size());
        bean.setNumOfProject(1);
        long bathRooms = quote.getRooms()
                .stream()
                .filter(room -> propertiesUtils.getProperty("room.bathRooms").contains(room.getRoomType()))
                .count();
        bean.setNumOfBathroom((int) bathRooms);
        long kitchens = quote.getRooms()
                .stream()
                .filter(room -> room.getRoomType().equalsIgnoreCase(propertiesUtils.getProperty("room.kitchen")))
                .count();
        bean.setNumOfKitchen((int) kitchens);
        bean.setHouseArea(quote.getInnerArea());
        return bean;
    }


    private void getConstructsForRoom(String decorationCompany,
                                      Long quoteVersionId,
                                      Long decorationTypeId,
                                      Long quoteTypeId,
                                      QuoteRoomDto room,
                                      CalculatorVarBean varValueBean,
                                      Long houseTypeId) {
        List<ConstructRelationship> roomConstructs = Lists.newArrayList();
        //获得空间绑定的施工项列表，循环绑定的施工项列表进行操作
        BasicDBObject basicDBObject = new BasicDBObject();
        //houseTypeId为null的是查询全部户型的施工项带出关系
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), decorationCompany);
        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quoteTypeId);
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), new BasicDBObject("$eq", null));
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindRooms.roomType"), room.getRoomType());
        List<ConstructRelationship> allRoomConstructs = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructRelationship.class);
        if (!CollectionUtils.isEmpty(allRoomConstructs)) {
            roomConstructs.addAll(allRoomConstructs);
        }
        basicDBObject.put(propertiesUtils.getProperty("houseType.houseTypeId"), houseTypeId);
        List<ConstructRelationship> exactRoomConstructs = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructRelationship.class);
        if (!CollectionUtils.isEmpty(exactRoomConstructs)) {
            roomConstructs.addAll(exactRoomConstructs);
        }
        if (CollectionUtils.isEmpty(roomConstructs)) {
            logger.info("{}没有绑定的要带出的施工项", room.getRoomType());
        } else {
            roomConstructs.stream().forEach(con -> {
                ConstructPo construct = cacheUtil.getCachedIndiviConstruct(con.getConstructCode());
                BindingConstruct bindingConstruct = null;
                if (construct != null) {
                    bindingConstruct = new BindingConstruct();
                    bindingConstruct.setConstructCode(construct.getConstructCode());
                    bindingConstruct.setConstructId(construct.getConstructId());
                    bindingConstruct.setConstructName(construct.getConstructName());
                    bindingConstruct.setDesc(construct.getDesc());
                    bindingConstruct.setAssitSpec(construct.getAssitSpec());
                    bindingConstruct.setUnitCode(construct.getUnitCode());
                }else{
                    logger.error("施工项自动带出-获取缓存施工项失败  constructCode:{}",con.getConstructCode());
                    return;
                }
                // 如果施工项已存在则只重新计算数量
                if (room.getConstructList().contains(bindingConstruct)) {
                    bindingConstruct = room.getConstructList().get(room.getConstructList().indexOf(bindingConstruct));
                    BigDecimal constructQuantity = BigDecimal.ZERO;
                    try {
                        constructQuantity = constructService.calConstruct(construct.getConstructId(),
                                new CalObjectValueBean(varValueBean));
                        bindingConstruct.setUsedQuantity(constructQuantity);
                    } catch (PackageException e) {
                        logger.error("计算施工项{}数量错误{}", construct.getConstructCode(), e.getErrorMsg());
                    }
                    return;
                }
                // 施工项不存在继续获取施工项价格
                Properties properties = propertiesUtils.buildProperties(new String[]{
                                propertiesUtils.getProperty("quoteVersion.quoteVersionId"),
                                propertiesUtils.getProperty("quoteType.quoteTypeId"),
                                propertiesUtils.getProperty("decorationType.decorationTypeId"),
                                propertiesUtils.getProperty("construct.constructCode"),
                                propertiesUtils.getProperty("common.decorationCompany"),
                                propertiesUtils.getProperty("common.status")
                        },
                        new Object[]{
                                quoteVersionId==null?0L:quoteVersionId,
                                quoteTypeId,
                                decorationTypeId,
                                construct.getConstructCode(),
                                decorationCompany,
                                CommonStaticConst.Common.ENABLE_STATUS
                        });
                try {
                    ConstructPricePo constructPrice = findPoByUniqueKey(properties, ConstructPricePo.class);
                    if (constructPrice == null) {
                        logger.error("获取施工项{}价格不存在", construct.getConstructCode());
                        bindingConstruct.setUnitPrice(BigDecimal.ZERO);
                        bindingConstruct.setCustomerPrice(BigDecimal.ZERO);
                        bindingConstruct.setForemanPrice(BigDecimal.ZERO);
                    } else {
                        bindingConstruct.setUnitPrice(constructPrice.getCustomerPrice());
                        bindingConstruct.setCustomerPrice(constructPrice.getCustomerPrice());
                        bindingConstruct.setForemanPrice(constructPrice.getForemanPrice());
                    }
                } catch (QuoteException e) {
                    logger.error("获取施工项{}价格错误{}", construct.getConstructCode(), e.getMessage());
                }
                //计算施工项数量
                try {
                    BigDecimal constructQuantity = constructService.calConstruct(construct.getConstructId(),
                            new CalObjectValueBean(varValueBean));
                    bindingConstruct.setUsedQuantity(constructQuantity);
                } catch (PackageException e) {
                    logger.error("计算施工项{}数量错误{}", construct.getConstructCode(), e.getMessage());
                }
                room.getConstructList().add(bindingConstruct);
            });
        }
    }


    /**
     * 根据roomId查询空间主材信息
     *
     * @param quoteRoomId 空间roomId
     * @return
     */
    @LogAnnotation
    public List<QuoteRoomMaterialPo> listQuoteRoomMaterialPo(Long quoteRoomId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("quoteRoomId", quoteRoomId);
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuoteRoomMaterialPo.class);
    }

    /**
     * 加载主材的品牌等信息
     * 因为mongo存储大小限制，目前主材存的是品牌的ID集合，前端显示需要根据品牌ID集合去数据库抓去相应 的品牌和产品信息
     * type 1套餐内主材.2套餐外主材
     *
     * @param materialPos
     */
    private void initMaterialBrandList(List<QuoteRoomMaterialPo> materialPos) {
        if (!CollectionUtils.isEmpty(materialPos)) {
            List<Long> materialPoIdList = materialPos.stream().map(QuoteRoomMaterialPo::getQuoteRoomMaterialId).collect(Collectors.toList());
            List<QuoteRoomMaterialBrandPo> brandList = getBrandByMaterialIds(materialPoIdList);
            materialPos.stream().forEach(
                    material -> {
                        material.setBrandList(
                                brandList.stream().filter(brand -> brand.getQuoteRoomMaterialId().equals(material.getQuoteRoomMaterialId())).collect(Collectors.toList())
                        );
                        List<QuoteRoomMaterialBrandPo> selectBrandList = material.getBrandList().stream().
                                filter(QuoteRoomMaterialBrandPo::getSelected).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(selectBrandList)) {
                            initSelectBrand(material, selectBrandList);
                        }
                        if (!CollectionUtils.isEmpty(material.getConstructList())) {
                            material.getConstructList().forEach(quoteRoomConstructPo ->
                                    initConstructPo(quoteRoomConstructPo, material.getQuoteRoomId())
                            );
                        }

                    }
            );
        }
    }

    /**
     * 根据materialId查询brand信息
     *
     * @param materialIds
     * @return
     */
    private List<QuoteRoomMaterialBrandPo> getBrandByMaterialIds(List<Long> materialIds) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("quoteRoomMaterialId", new BasicDBObject("$in", materialIds));
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuoteRoomMaterialBrandPo.class);
    }

    /**
     * 根据roomId查询空间软装信息
     *
     * @param quoteRoomId 空间roomId
     * @return
     */
    private List<QuoteRoomTextilePo> listQuoteRoomTextilePo(Long quoteRoomId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("quoteRoomId", quoteRoomId);
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuoteRoomTextilePo.class);
    }

    /**
     * 初始化选中品牌信息
     *
     * @param material
     * @param selectBrandList
     */
    private void initSelectBrand(QuoteRoomMaterialPo material, List<QuoteRoomMaterialBrandPo> selectBrandList) {
        BigDecimal usedQuantity = BigDecimal.ZERO;
        List<BigDecimal> prices = Lists.newArrayList();
        for (QuoteRoomMaterialBrandPo quoteRoomMaterialBrandPo : selectBrandList) {
            List<Product> selectProduct = quoteRoomMaterialBrandPo.getProduct().stream().filter(Product::getSelected).collect(Collectors.toList());
            for (Product product : selectProduct) {
                usedQuantity = usedQuantity.add(product.getUsedQuantity());
                product.setPrice(StringUtils.isEmpty(product.getMdmSku()) ? BigDecimal.ZERO : materialService.getPriceByMdmSku(product.getMdmSku()));
                prices.add(product.getPrice());
            }
        }
        BigDecimal max = Collections.max(prices);
        material.setUnitPrice(max);
        material.setUsedQuantity(usedQuantity);
    }

    /**
     * 初始化施工项的信息，从本地缓存中读取
     *
     * @param constructPo
     * @param quoteRoomId
     */
    private void initConstructPo(QuoteRoomConstructPo constructPo, Long quoteRoomId) {
        Construct construct = cacheUtil.getCachedConstruct(constructPo.getCid().toString());
        if (construct != null) {
            constructPo.setConstructCode(construct.getConstructCode());
            constructPo.setConstructName(construct.getConstructName());
            constructPo.setUnit(construct.getUnit());
            constructPo.setUnitCode(construct.getUnitCode());
            constructPo.setDescription(construct.getDescription());
            constructPo.setRemark(construct.getRemark());
            constructPo.setQuoteRoomId(quoteRoomId);
            constructPo.setForemanPrice(construct.getForemanPrice());
            constructPo.setCustomerPrice(construct.getCustomerPrice());
            constructPo.setUnitPrice(construct.getCustomerPrice());
        }
    }


    /**
     * 保存更新报价详细信息
     *
     * @param quoteDetail 报价实体
     * @throws QuoteException
     */
    @LogAnnotation
    public CommonRespBean updateQuoteDetailInfo(QuoteDetailDto quoteDetail) throws QuoteException {
        try {
            QuoteDocumentPo quote = getQuotePoById(quoteDetail.getQuoteId());
            quote.setRooms(quoteDetail.getRoomList());
            quote.setExtraCharges(quoteDetail.getExtraCharges());
            quote.setStep(3);
            updateQuote(quote, Optional.of("quoteId"), Optional.of(quoteDetail.getQuoteId()));
        } catch (Exception e) {
            logger.error("更新报价详情出错啦{}", e.getMessage());
            throw new QuoteException("更新报价详情出错啦");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "保存报价成功"));
    }

    /**
     * 根据quoteId查询quoteRoomPo
     *
     * @param quoteId 报价id
     * @return
     */
    @LogAnnotation
    public List<QuoteRoomPo> listQuoteRoomPo(Long quoteId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("quoteId", quoteId);
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuoteRoomPo.class);
    }

    /**
     * 计算主材的价钱，超出或者套餐外主材金额
     *
     * @param ciList
     * @param type   1是超出主材，2是套餐外主材也可计算全部主材
     * @return
     */
    private BigDecimal calculateMaterialPrice(List<QuoteRoomMaterialPo> ciList, Integer type) {
        BigDecimal price = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(ciList)) {
            //遍历主材
            for (QuoteRoomMaterialPo quoteRoomMaterialPo : ciList) {
                List<QuoteRoomMaterialBrandPo> collect = quoteRoomMaterialPo.getBrandList().stream().filter(QuoteRoomMaterialBrandPo::getSelected).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    //遍历主材下面的品牌
                    for (QuoteRoomMaterialBrandPo quoteRoomMaterialBrandPo : collect) {
                        //过滤出品牌下面被选中的单品
                        List<Product> product = quoteRoomMaterialBrandPo.getProduct().stream().filter(Product::getSelected).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(product)) {
                            List<BigDecimal> prices = Lists.newArrayList();
                            //遍历选中的单品获取价格集合以便获取最高值
                            for (Product p : product) {
                                prices.add(StringUtils.isEmpty(p.getMdmSku()) ? BigDecimal.ZERO : p.getPrice());
//                                prices.add(StringUtils.isEmpty(p.getMdmSku()) ? BigDecimal.ZERO :getPriceByMdmSku(p.getMdmSku()));
                            }
                            BigDecimal max = Collections.max(prices);
                            quoteRoomMaterialPo.setUnitPrice(max);
                            if (type.equals(1)) {
                                price = price.add(max.multiply(quoteRoomMaterialPo.getUsedQuantity().subtract(quoteRoomMaterialPo.getLimitQuantity())));
                                break;
                            }
                            if (type.equals(2)) {
                                for (Product p : product) {
                                    price = price.add(StringUtils.isEmpty(p.getMdmSku()) ? BigDecimal.ZERO : p.getPrice().multiply(p.getUsedQuantity()));
//                                    price = price.add(StringUtils.isEmpty(p.getMdmSku()) ? BigDecimal.ZERO : getPriceByMdmSku(p.getMdmSku()).multiply(p.getUsedQuantity()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return price;
    }

    /**
     * 根据sku去mdm获取价格
     *
     * @param sku 单品sku
     * @return
     */
    @Deprecated
    private BigDecimal getPriceByMdmSku(String sku) {
        BigDecimal productPrice = BigDecimal.ZERO;
        if (StringUtils.isEmpty(sku) || "default".equals(sku)) {
            logger.info("sku不合法：sku = {}", sku);
            return productPrice;
        }
        try {
            PriceViewResponseBean priceViewResponseBean = priceFeignClient.getPriceByProductId(sku, Constants.REGION_CODE);
            if (priceViewResponseBean != null && !CollectionUtils.isEmpty(priceViewResponseBean.getPrices()) && priceViewResponseBean.getPrices().get(0) != null) {
                productPrice = priceViewResponseBean.getPrices().get(0).getPrice();
            }
        } catch (Exception e) {
            logger.error("通过sku:{} 获取主材出现异常:{}", sku, e);
        }
        return productPrice;
    }


    /**
     * 计算软装金额
     *
     * @param ciList
     * @param
     * @return
     */
    private BigDecimal calculateHouseTextilePrice(List<QuoteRoomTextilePo> ciList) {
        BigDecimal price = BigDecimal.ZERO;
        if (ciList != null) {
            for (QuoteRoomTextilePo po : ciList) {
                if (po.getTextilePrice() == null || po.getTextileNum() == null) {
                    continue;
                }
                price = price.add(po.getTextilePrice().multiply(po.getTextileNum()));
            }
        }
        return price;
    }


    /**
     * 计算施工项的价钱，超出或者套餐外施工项
     *
     * @param ciList
     * @param type   1是超出施工项，2的套餐外施工项
     * @return
     */
    public BigDecimal calcCiPrice(List<QuoteRoomConstructPo> ciList, Integer type) {
        BigDecimal price = BigDecimal.ZERO;
        for (QuoteRoomConstructPo po : ciList) {
            if (po.getUnitPrice() == null || po.getUsed() == null || po.getLimit() == null) {
                continue;
            }
            if (type.equals(1)) {
                price = price.add(po.getUnitPrice().multiply(po.getUsed().subtract(po.getLimit())));
            }
            if (type.equals(2)) {
                price = price.add(po.getUnitPrice().multiply(po.getUsed()));
            }
        }
        return price;
    }


    /**
     * 更新productList的价格信息
     *
     * @param productList
     * @return
     */
    private List<Product> updateProductPrice(List<Product> productList) {
        StringBuffer ids = new StringBuffer();
        for (Product product : productList) {
            if (StringUtils.isEmpty(product.getMdmSku()) || "default".equals(product.getMdmSku())) {
                logger.info("sku不存在或为default sku= {} ", product.getMdmSku());
                continue;
            }
            ids = ids.append(product.getMdmSku()).append(",");
        }
        if (StringUtils.isEmpty(ids.toString())) {
            return productList;
        }
        String skuList = ids.substring(0, ids.length() - 1);
        List<PriceViewResponseBean> priceViewResponseBeanList = priceFeignClient.getPriceByProductIds(skuList, Constants.REGION_CODE);
        if (priceViewResponseBeanList == null) {
            return productList;
        }
        productList.stream().forEach(product -> {
            if (!StringUtils.isEmpty(product.getMdmSku()) || "default".equals(product.getMdmSku())) {
                priceViewResponseBeanList.stream().forEach(price -> {
                    if (price.getProductId().equals(product.getMdmSku())) {
                        if (!CollectionUtils.isEmpty(price.getPrices()) && price.getPrices().get(0).getPrice() != null) {
                            product.setPrice(price.getPrices().get(0).getPrice());
                        }
                    }
                });
            }
        });
        return productList;
    }

    /**
     * 更新主材内单品的价格
     *
     * @param materialList 主材列表
     * @param productList  有价格单品集合
     */
    private void updateMaterialPoProductPrice(List<QuoteRoomMaterialPo> materialList, List<Product> productList) {
        materialList.stream().forEach(material -> {
            material.getBrandList().stream().forEach(brand -> {
                brand.getProduct().stream().forEach(product -> {
                    productList.stream().forEach(p -> {
                        if (!StringUtils.isEmpty(p.getMdmSku()) && !StringUtils.isEmpty(product.getMdmSku()) && p.getMdmSku().equals(product.getMdmSku())) {
                            product.setPrice(p.getPrice());
                        }
                    });
                });
            });
        });
    }


    /**
     * 对于家纺（软装）根据sku批量查询
     *
     * @param quoteRoomTextilePoList
     * @return
     */
    private List<QuoteRoomTextilePo> getTextileByListIds(List<QuoteRoomTextilePo> quoteRoomTextilePoList) {
        List<QuoteRoomTextilePo> quoteRoomTextilePos = new ArrayList<>();
        StringBuilder ids = new StringBuilder();
        if (quoteRoomTextilePoList == null) {
            return null;
        }
        for (QuoteRoomTextilePo quoteRoomTextilePo : quoteRoomTextilePoList) {
            //判断sku是否存在
            if (quoteRoomTextilePo.getProductSku() == null) {
                //不存在直接添加入集合返回，
                quoteRoomTextilePos.add(quoteRoomTextilePo);
            } else {
                ids.append(quoteRoomTextilePo.getProductSku()).append(",");
            }
        }
        if (ids.length() > 0) {
            String id = ids.substring(0, ids.toString().length() - 1);

            //从主数据查询材料信息
            HttpSingleResponse<ListResponse<TextileRespBean>> httpSingleResponse = catentryFeignClient.textileById(id);
            if (httpSingleResponse == null) {
                return null;
            }
            ListResponse<TextileRespBean> singleResponse = httpSingleResponse.getData();

            //查询材料价格
            List<PriceViewResponseBean> priceViewResponseBeanList = priceFeignClient.getPriceByProductIds(id, Constants.REGION_CODE);
            if (priceViewResponseBeanList == null) {
                return null;
            }
            List<TextileRespBean> textileRespBeans = singleResponse.getDataList();

            PriceViewResponseBean removePriceViewBean = new PriceViewResponseBean();
            for (TextileRespBean textileRespBean : textileRespBeans) {
                QuoteRoomTextilePo quoteRoomTextilePo = new QuoteRoomTextilePo();

                //判断sku是否存在于主数据
                if (textileRespBean.getTextileName() != null) {
                    BeanUtils.copyProperties(textileRespBean, quoteRoomTextilePo);
                    quoteRoomTextilePo.setProductSku(textileRespBean.getCtentryId());

                    //材料图片的拼接
                    if (quoteRoomTextilePo.getTextileImage() != null && quoteRoomTextilePo.getTextileImage().substring(0, 3).equals(Constants.IMG_MANE)) {
                        StringBuffer imageLink = new StringBuffer(Constants.IMG_LINK);
                        imageLink.append(quoteRoomTextilePo.getTextileImage());
                        quoteRoomTextilePo.setTextileImage(imageLink.toString());
                    }

                    //材料所属房间
                    for (QuoteRoomTextilePo textile : quoteRoomTextilePoList) {
                        if (textileRespBean.getCtentryId().equals(textile.getProductSku())) {
                            quoteRoomTextilePo.setQuoteRoomId(textile.getQuoteRoomId());
                            quoteRoomTextilePo.setQuoteRoomTextileId(textile.getQuoteRoomTextileId());
                            quoteRoomTextilePo.setTextileNum(textile.getTextileNum());
                        }
                    }

                    //查询材料所属地区价格
                    for (PriceViewResponseBean priceViewResponseBean : priceViewResponseBeanList) {
                        if (quoteRoomTextilePo.getProductSku().equals(priceViewResponseBean.getProductId())) {
                            quoteRoomTextilePo.setTextilePrice(priceViewResponseBean.getPrices().get(0).getPrice());
                            removePriceViewBean = priceViewResponseBean;
                        }
                    }
                    if (removePriceViewBean != null) {
                        priceViewResponseBeanList.remove(removePriceViewBean);
                    }
                } else {
                    QuoteRoomTextilePo quoteRoomTextileBean = new QuoteRoomTextilePo();
                    for (QuoteRoomTextilePo quoteTextile : quoteRoomTextilePoList) {
                        if (textileRespBean.getCtentryId().equals(quoteTextile.getProductSku())) {
                            quoteRoomTextilePo = quoteTextile;
                            quoteRoomTextileBean = quoteTextile;
                        }
                    }
                    if (quoteRoomTextileBean != null) {
                        quoteRoomTextilePoList.remove(quoteRoomTextileBean);
                    }
                }
                quoteRoomTextilePos.add(quoteRoomTextilePo);
            }
        }
        return quoteRoomTextilePos;
    }


    /**
     * 深赋值
     *
     * @param src
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<GetQuoteRoomDetailDto> deepCopy(List<GetQuoteRoomDetailDto> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        List<GetQuoteRoomDetailDto> dest = (List<GetQuoteRoomDetailDto>) in.readObject();
        return dest;
    }


    /**
     * 对于家纺（软装）根据名称模糊查询
     * 通过3D方
     */
    public HttpResponseList getTextileByName(Long quoteRoomId, String textileName, String tag, long offset, long limit) throws QuoteException {
        long pageNum = 0;
        if (offset > 1) {
            pageNum = (offset - 1) * limit;
        }
        //创建对象
        HttpResponseList<TextilesRespBean> httpResponseList = new HttpResponseList<>();
        List<TextilesRespBean> textilesRespBeanList = new ArrayList<>();

        //查询软装数据
        StringBuffer searchUrl = new StringBuffer(textileThirdUrlConfig.getMiniProductUrl() + "?offset=" + pageNum + "&limit=" + limit + "&" + textileThirdUrlConfig.getMiniProductType());
        if (!org.apache.commons.lang.StringUtils.isEmpty(textileName)) {
            searchUrl.append("&t=" + textileName);
        }
        HttpResponse response = HttpSendUtil.sendHttpGet(searchUrl.toString());
        //解析数据
        if (response != null && response.getStatus() == HttpStatus.SC_OK) {
            JSONObject map = JSON.parseObject(response.getContent());
            com.alibaba.fastjson.JSONArray items = map.getJSONArray("items");
            if (items == null) {
                throw new QuoteException(QuoteErrorEnum.QUOTE_NOT_FOUND);
            }
            long total = map.getLongValue("total");
            //总条数
            httpResponseList.setPaging(new PagingData((int) offset, (int) limit, total));
            //查询到的数据集合
            List<MiniProductRespBean> miniProductRespBeanList = items.toJavaList(MiniProductRespBean.class);
            for (MiniProductRespBean miniProductRespBean : miniProductRespBeanList) {
                TextilesRespBean textilesRespBean = new TextilesRespBean();
                textilesRespBean.setQuoteRoomId(quoteRoomId);
                textilesRespBean.setQuoteRoomTextileId(keyManagerUtil.get16PlacesId());
                textilesRespBean.setTextileName(miniProductRespBean.getName());
                textilesRespBean.setProductSku(miniProductRespBean.getSku());
                textilesRespBean.setTextileImage(miniProductRespBean.getImageResized());
                textilesRespBean.setBrand(miniProductRespBean.getVendor());
                //价格查询
                List<PriceViewResponseBean> priceViewResponseBeanList = priceFeignClient.getPriceByProductIds(textilesRespBean.getProductSku(), Constants.REGION_CODE);
                if (priceViewResponseBeanList != null && priceViewResponseBeanList.size() > 0) {
                    textilesRespBean.setTextilePrice(priceViewResponseBeanList.get(0).getPrices().get(0).getPrice());
                }
                //查询是否不为居然商品
                HttpSingleResponse<ListResponse<TextileRespBean>> singleResponse = catentryFeignClient.textileById(textilesRespBean.getProductSku().toString());
                if (singleResponse != null) {
                    ListResponse<TextileRespBean> listResponse = singleResponse.getData();
                    List<TextileRespBean> textileRespBeans = listResponse.getDataList();
                    if (textileRespBeans.get(0).getTbId() != null || textileRespBeans.get(0).getTbSkuId() != null) {
                        textilesRespBean.setTbId(textileRespBeans.get(0).getTbId());
                        textilesRespBean.setTbSkuId(textileRespBeans.get(0).getTbSkuId());
                    }
                }
                //加入集合
                textilesRespBeanList.add(textilesRespBean);
            }
            httpResponseList.setData(textilesRespBeanList);
        } else {
            throw new QuoteException(QuoteErrorEnum.QUOTE_NOT_FOUND);
        }
        return httpResponseList;
    }

    /**
     * 根据designId获取报价分享页url
     *
     * @param designIds 案例集合
     * @return 分享页面url
     */
    @LogAnnotation
    public List<QuoteShareUrlRespBean> getQuoteShareUrl(String designIds) {
        if (!StringUtils.isEmpty(designIds)) {
            String[] split = designIds.split(",");
            ArrayList<String> list = Lists.newArrayList(split);
            ArrayList<QuoteShareUrlRespBean> result = Lists.newArrayList();

            //获取930案例报价的url
            List<QuotePo> quotePos = getQuotePoByDesignIds(list);
            if (!CollectionUtils.isEmpty(quotePos)) {
                list.forEach(id -> {
                    QuoteShareUrlRespBean quoteShareUrlRespBean = new QuoteShareUrlRespBean();
                    quoteShareUrlRespBean.setDesignId(id);
                    String quoteUrl = null;
                    if (!CollectionUtils.isEmpty(quotePos)) {
                        List<QuotePo> quotePoList = quotePos.stream().filter(quotePo -> quotePo.getDesignId().equals(id)).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(quotePoList)) {
                            QuotePo po = quotePoList.get(0);
                            quoteUrl = QuoteUtils.stringFormat(quoteConfig.getShareUrl(), new Object[]{Long.toString(po.getQuoteId())});
                        }
                    }
                    quoteShareUrlRespBean.setShareUrl(quoteUrl);
                    result.add(quoteShareUrlRespBean);
                });
            }

            //获取个性化报价的url
            List<QuoteDocumentPo> quoteDocumentPos = getQuoteDocumentsByDesignIds(list);
            if (!CollectionUtils.isEmpty(quoteDocumentPos)) {
                list.forEach(id -> {
                    QuoteShareUrlRespBean quoteShareUrlRespBean = new QuoteShareUrlRespBean();
                    quoteShareUrlRespBean.setDesignId(id);
                    String quoteUrl = null;
                    if (!CollectionUtils.isEmpty(quoteDocumentPos)) {
                        List<QuoteDocumentPo> quotePoList = quoteDocumentPos.stream().filter(quotePo -> quotePo.getDesignId().equals(id)).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(quotePoList)) {
                            QuoteDocumentPo po = quotePoList.get(0);
                            quoteUrl = QuoteUtils.stringFormat(quoteConfig.getQuoteResultUrl(), new Object[]{Long.toString(po.getQuoteId())});
                        }
                    }
                    quoteShareUrlRespBean.setShareUrl(quoteUrl);
                    result.add(quoteShareUrlRespBean);
                });
            }
            return result;
        }
        return null;
    }


    public CommonRespBean getQuoteSummary(Long quoteId) throws QuoteException {
        BigDecimal bagOverPrice = BigDecimal.ZERO;
        Properties properties = new Properties();
        properties.put(propertiesUtils.getProperty("quote.id"), quoteId);
        QuotePo quotePo = findPoByUniqueKey(properties, QuotePo.class);
        if (quotePo == null) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "案例报价不存在"));
        }
        List<PackageBag> packageBags = packageService.queryPackageBagByVersionId(quotePo.getPackageVersionId());
        GetQuoteSummaryDto dto = null;
        try {
            dto = getQuoteSummaryDto(quotePo, bagOverPrice, packageBags);
        } catch (QuoteException e) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, e.getErrorMsg()));
        }
        bagOverPrice = dto.getPrice().getOverBagPrice();
        //前端展示优化，施工项超量的话需要拆分成两个，一个是显示套餐内使用数量，价格为0，另外一个是显示超量的使用数量，价格为正常价格
        List<QuoteRoomConstructPo> finallyConstructItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dto.getConstructionItems())) {
            dto.getConstructionItems().forEach(quoteRoomConstructPo ->
                    {
                        QuoteRoomConstructPo quoteRoomConstructPo1 = new QuoteRoomConstructPo();
                        BeanUtils.copyProperties(quoteRoomConstructPo, quoteRoomConstructPo1);
                        if (quoteRoomConstructPo.getLimit() != null && quoteRoomConstructPo.getUsed() != null &&
                                quoteRoomConstructPo.getLimit().compareTo(BigDecimal.ZERO) > 0 &&
                                quoteRoomConstructPo.getUsed().compareTo(quoteRoomConstructPo.getLimit()) > 0) {
                            quoteRoomConstructPo1.setUnitPrice(BigDecimal.ZERO);
                            if (quoteRoomConstructPo.getLimit() != null) {
                                quoteRoomConstructPo1.setUsed(quoteRoomConstructPo.getLimit().setScale(2, BigDecimal.ROUND_HALF_UP));
                            } else {
                                quoteRoomConstructPo1.setUsed(BigDecimal.ZERO);
                            }
                        } else {
                            quoteRoomConstructPo1.setUnitPrice(BigDecimal.ZERO);
                        }
                        finallyConstructItems.add(quoteRoomConstructPo1);
                    }
            );
            dto.setConstructionItems(finallyConstructItems);
        }
        if (!CollectionUtils.isEmpty(dto.getCiOverLimit())) {
            dto.getCiOverLimit().forEach(quoteRoomConstructPo ->
                    quoteRoomConstructPo.setUsed(quoteRoomConstructPo.getUsed().subtract(quoteRoomConstructPo.getLimit()))
            );
        }
        //使用数量只显示未超量的数量，如果超量则显示limitQuantity
        if (!CollectionUtils.isEmpty(dto.getInnerMaterialsItems())) {
            dto.getInnerMaterialsItems().forEach(quoteRoomMaterialPo ->
                    {
                        if (quoteRoomMaterialPo.getLimitQuantity() != null &&
                                quoteRoomMaterialPo.getLimitQuantity().compareTo(BigDecimal.ZERO) > 0 &&
                                quoteRoomMaterialPo.getUsedQuantity() != null &&
                                quoteRoomMaterialPo.getUsedQuantity().compareTo(quoteRoomMaterialPo.getLimitQuantity()) > 0) {
                            quoteRoomMaterialPo.setUsedQuantity(quoteRoomMaterialPo.getLimitQuantity().setScale(2, RoundingMode.HALF_UP));
                        }
                    }
            );
        }
        //如果大礼包超过价钱的话，需要摊分
        //大礼包钱之内的施工项金额显示0，之外的分摊到某个施工项上
        if (bagOverPrice.compareTo(BigDecimal.ZERO) > 0) {
            List<QuoteRoomConstructPo> finallyCiGiftItems = new ArrayList<>();
            if (!CollectionUtils.isEmpty(packageBags)) {
                for (PackageBag packageBag : packageBags) {
                    if (packageBag.getAmount() == null) {
                        continue;
                    }
                    List<QuoteRoomConstructPo> collect = dto.getCiGifts().stream().filter(quoteRoomConstructPo -> packageBag.getBagId().equals(quoteRoomConstructPo.getBagId())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        for (QuoteRoomConstructPo quoteRoomConstructPo : collect) {
                            if (bagOverPrice.compareTo(BigDecimal.ZERO) > 0) {
                                if (quoteRoomConstructPo.getUnitPrice() != null && quoteRoomConstructPo.getUsed() != null) {
                                    BigDecimal constructPrice = quoteRoomConstructPo.getUnitPrice().multiply(quoteRoomConstructPo.getUsed());
                                    //如果当前施工项的总价格小于超出的金额，直接复制实体类添加到list，并且bagOverPrice相减
                                    if (constructPrice.compareTo(bagOverPrice) <= 0) {
                                        QuoteRoomConstructPo quoteRoomConstructPo1 = new QuoteRoomConstructPo();
                                        BeanUtils.copyProperties(quoteRoomConstructPo, quoteRoomConstructPo1);
                                        finallyCiGiftItems.add(quoteRoomConstructPo1);
                                        bagOverPrice = bagOverPrice.subtract(constructPrice);
                                    } else {
                                        //套餐内的价钱,根据超量的价格算出套餐内的数量，金额为0
                                        BigDecimal packageGiftPrice = constructPrice.subtract(bagOverPrice);
                                        QuoteRoomConstructPo quoteRoomConstructPo1 = new QuoteRoomConstructPo();
                                        BeanUtils.copyProperties(quoteRoomConstructPo, quoteRoomConstructPo1);
                                        quoteRoomConstructPo1.setUnitPrice(BigDecimal.ZERO);
                                        quoteRoomConstructPo1.setUsed(packageGiftPrice.divide(quoteRoomConstructPo.getUnitPrice(), 2, BigDecimal.ROUND_HALF_UP));
                                        finallyCiGiftItems.add(quoteRoomConstructPo1);
                                        QuoteRoomConstructPo quoteRoomConstructPo2 = new QuoteRoomConstructPo();
                                        BeanUtils.copyProperties(quoteRoomConstructPo, quoteRoomConstructPo2);
                                        quoteRoomConstructPo2.setUsed(bagOverPrice.divide(quoteRoomConstructPo.getUnitPrice(), 2, BigDecimal.ROUND_HALF_DOWN));
                                        finallyCiGiftItems.add(quoteRoomConstructPo2);
                                        bagOverPrice = BigDecimal.ZERO;
                                    }
                                }
                            }
                            //如果大礼包超出金额已经分摊完毕，剩下的都是大礼包之内的施工项，金额改为0即可
                            else {
                                QuoteRoomConstructPo quoteRoomConstructPo1 = new QuoteRoomConstructPo();
                                BeanUtils.copyProperties(quoteRoomConstructPo, quoteRoomConstructPo1);
                                quoteRoomConstructPo1.setUnitPrice(BigDecimal.ZERO);
                                finallyCiGiftItems.add(quoteRoomConstructPo1);
                            }
                        }
                    }
                }
            }
            dto.setCiGifts(finallyCiGiftItems);
        }
        //以下为施工项按照项目编码排序，套餐内，超量的，自定义和大礼包
        dto.getConstructionItems().sort(Comparator.comparing(QuoteRoomConstructPo::getConstructCode));
        dto.getCiGifts().sort(Comparator.comparing(QuoteRoomConstructPo::getConstructCode));
        dto.getCiOverLimit().sort(Comparator.comparing(QuoteRoomConstructPo::getConstructCode));
        dto.getCiManual().sort(Comparator.comparing(QuoteRoomConstructPo::getConstructCode));
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取报价成功"), dto);

    }


    /**
     * 提交报价，计算价格包括：
     * 1.报价总价（实际报价）
     * 2.报价参考总价
     * 3.施工项总报价
     * 4.每个房间内的报价
     * 5.软装报价
     * 6.硬装报价
     * 7.材料总价
     *
     * @param quoteId
     * @throws QuoteException
     */
    public CommonRespBean submitQuote(Long quoteId) throws QuoteException {
        QuoteDocumentPo quote = getQuotePoById(quoteId);
        BigDecimal constructTotalPrice = BigDecimal.ZERO;//施工项总价
        BigDecimal totalPrice = BigDecimal.ZERO;//报价总价
        BigDecimal hardDecorationTotalPrice = BigDecimal.ZERO;//硬装总价
        BigDecimal softDecorationTotalPrice = BigDecimal.ZERO;//软装总价
        BigDecimal decorationMaterialTotalPrice = BigDecimal.ZERO;//所有材料总价
        BigDecimal hardDecorationMaterialTotalPrice = BigDecimal.ZERO;
        for (QuoteRoomDto quoteRoom : quote.getRooms()) {
            BigDecimal constructPriceInRoom = BigDecimal.ZERO;
            //计算房间内空间带出的施工项总价
            for (BindingConstruct c : quoteRoom.getConstructList()) {
                if (c.getCustomerPrice() != null && c.getUsedQuantity() != null) {
                    constructPriceInRoom = constructPriceInRoom.add(c.getCustomerPrice().multiply(c.getUsedQuantity()));
                }
            }
            //计算房间内硬装主材价格
            BigDecimal hardMaterialPrice = BigDecimal.ZERO;
            for (DecorationMaterial m : quoteRoom.getHardDecorationMaterials()) {
                hardMaterialPrice = hardMaterialPrice.add(m.getUsedQuantity().multiply(m.getUnitPrice()));
            }
            //计算房间内软装价格
            BigDecimal softMaterialPrice = BigDecimal.ZERO;
            for (DecorationMaterial m : quoteRoom.getSoftDecorationMaterials()) {
                softMaterialPrice = softMaterialPrice.add(m.getUsedQuantity().multiply(m.getUnitPrice()));
            }

            hardDecorationMaterialTotalPrice = hardDecorationMaterialTotalPrice.add(hardMaterialPrice);
            quoteRoom.setTotalPrice(hardMaterialPrice
                    .add(softMaterialPrice)
                    .add(constructPriceInRoom)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
            quoteRoom.setHardMaterialPrice(hardMaterialPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
            quoteRoom.setSoftMaterialPrice(softMaterialPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
            quoteRoom.setConstructsPrice(constructPriceInRoom.setScale(2, BigDecimal.ROUND_HALF_UP));

            constructTotalPrice = constructTotalPrice.add(constructPriceInRoom);
            totalPrice = totalPrice.add(hardMaterialPrice).add(softMaterialPrice).add(constructPriceInRoom);
            hardDecorationTotalPrice = hardDecorationTotalPrice.add(hardMaterialPrice).add(constructPriceInRoom);
            softDecorationTotalPrice = softDecorationTotalPrice.add(softMaterialPrice);
            decorationMaterialTotalPrice = decorationMaterialTotalPrice.add(hardMaterialPrice).add(softMaterialPrice);
        }

        QuoteResult quoteResult = quote.getQuoteResult() == null ? new QuoteResult() : quote.getQuoteResult();
        quoteResult.setConstructionTotalPrice(constructTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setHardDecorationPrice(hardDecorationTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setSoftDecorationPrice(softDecorationTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setMaterialTotalPrice(decorationMaterialTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setHardMaterialPrice(hardDecorationMaterialTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setTotalPrice(totalPrice);

        //toDo: 计算优惠价格

        BigDecimal extraPrice = BigDecimal.ZERO;
        for (ChargeTypeDto chargeTypeDto : quote.getExtraCharges()) {
            extraPrice = extraPrice.add(calculateChargePrice(quoteResult,chargeTypeDto));
        }
        quote.setStep(4);
        quoteResult.setExtraPrice(extraPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        totalPrice = totalPrice.add(extraPrice);
        quoteResult.setTotalPrice(totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteResult.setUnitPrice(totalPrice.divide(quote.getInnerArea(), 2, RoundingMode.HALF_UP));
        quote.setQuoteResult(quoteResult);

        try {
            WriteResult writeResult = updateQuote(quote, Optional.of("quoteId"), Optional.of(quoteId));
            if (writeResult.getN() == 0) {
                logger.info("提交报价，更新报价失败,quoteId:{}.", quoteId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, "提交报价，更新报价失败"));
            } else {
                logger.info("提交报价更新报价成功,quoteId:{}.", quoteId);
            }
        } catch (Exception e) {
            throw new QuoteException("提交报价-更新报价出错");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "提交报价完成"));
    }

    private BigDecimal calculateChargePrice(QuoteResult quoteResult,ChargeTypeDto chargeTypeDto) {
        if(chargeTypeDto.getCalculationTypeCode().equals(ChargeCalculationTypeEnums.FIXED_CHARGE.getCode())) {
            return chargeTypeDto.getAmount();
        }
        else if(chargeTypeDto.getCalculationTypeCode().equals(ChargeCalculationTypeEnums.RATE_CHARGE.getCode())){
            if(chargeTypeDto.getRateBaseCode().equals(ChargeRateBaseEnums.BASE_ON_MATERIAL_CONSTRUCT_PRICE.getCode())){
                BigDecimal base = quoteResult.getConstructionTotalPrice().add(quoteResult.getHardMaterialPrice());
                return base.multiply(chargeTypeDto.getRate().divide(BigDecimal.valueOf(100)));
            } else if(chargeTypeDto.getRateBaseCode().equals(ChargeRateBaseEnums.BASE_ON_MATERIAL_PRICE.getCode())){
                return quoteResult.getHardMaterialPrice().multiply(chargeTypeDto.getRate().divide(BigDecimal.valueOf(100)));
            } else if(chargeTypeDto.getRateBaseCode().equals(ChargeRateBaseEnums.BASE_ON_CONSTRUCT_PRICE.getCode())){
                return quoteResult.getConstructionTotalPrice().multiply(chargeTypeDto.getRate().divide(BigDecimal.valueOf(100)));
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取报价结果详细信息
     *
     * @param quoteId
     * @return
     * @throws QuoteException
     */
    public CommonRespBean getQuoteResult(Long quoteId) throws QuoteException {
        try {
            if (redisLock.acquireAndRelease(quoteId.toString())) {
                QuoteDocumentPo quoteResult = getQuotePoById(quoteId);
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取报价结果成功"), quoteResult);
            }
        } catch (Exception e) {
            logger.error("获取报价结果信息异常{}", e.getMessage());
            throw new QuoteException("获取报价结果信息异常");
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, "获取报价结果失败"));
    }

    /**
     * 对接第三方瓶体获取报价结果详细信息
     *
     * @param designId
     * @return
     * @throws QuoteException
     */
    public CommonRespBean getCaseQuotation(String designId) throws QuoteException {
        CommonRespBean respBean;
        try {
            Properties properties = new Properties();
            properties.put(propertiesUtils.getProperty("quote.designId"), designId);
            QuoteDocumentPo quoteResult = findPoByUniqueKey(properties, QuoteDocumentPo.class);
            if (quoteResult == null) {
                String message = String.format("设计方案id:%s 报价不存在", designId);
                respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, message), quoteResult);
            } else {
                respBean = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "获取报价结果成功"), quoteResult);
            }
        } catch (Exception e) {
            logger.error("获取报价结果信息异常{}", e.getMessage());
            throw new QuoteException("获取报价结果信息异常");
        }
        return respBean;
    }

    /**
     * 判断设计方案是否存在面积小于1平米的房间
     *
     * @param quoteId
     * @return
     */
    public CommonRespBean verifyDesignRooms(Long quoteId) throws QuoteException {
        QuoteDocumentPo quote = getQuotePoById(quoteId);
        for (BomRoomDto room : quote.getDesignRooms()) {
            if (Double.valueOf(room.getArea()) < 1.0) {
                return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "存在面积小于1平米的房间"));
            }
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "不存在面积小于1平米的房间"));
    }

    private List<QuoteDocumentPo> getQuoteDocumentsByDesignIds(List<String> designIds) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("designId", new BasicDBObject("$in", designIds));
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuoteDocumentPo.class);
    }

    private List<QuotePo> getQuotePoByDesignIds(List<String> designIds) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("designId", new BasicDBObject("$in", designIds));
        return mongoTemplate.find(new BasicQuery(basicDBObject), QuotePo.class);
    }

    private GetQuoteSummaryDto getQuoteSummaryDto(QuotePo quotePo, BigDecimal bagOverPrice, List<PackageBag> packageBags) throws QuoteException {
        GetQuoteSummaryDto dto = new GetQuoteSummaryDto();
        List<GetQuoteRoomDetailDto> roomList = getQuoteRoomDetail(quotePo.getQuoteId());
        dto.setQuotationGmtCreate(DateUtils.dateToString(quotePo.getCreateDate()));
        dto.setQuotationGmtModify(DateUtils.dateToString(quotePo.getUpdateDate()));
        dto.setDesignId(quotePo.getDesignId());
        dto.setQuoteId(quotePo.getQuoteId());
        //查询客户基本信息
        dto.setCustomerInfo(getQuoteBase(quotePo.getQuoteId()));
        return calculateQuoteSummary(dto, roomList, quotePo, bagOverPrice, packageBags);
    }

    private GetQuoteBaseInfoRespBean getQuoteBase(Long quoteId) throws QuoteException {
        Properties properties = new Properties();
        properties.put(propertiesUtils.getProperty("quote.id"), quoteId);
        QuotePo quotePo = findPoByUniqueKey(properties, QuotePo.class);
        GetQuoteBaseInfoRespBean result = new GetQuoteBaseInfoRespBean();
        result.setInnerArea(quotePo.getInnerArea());
        result.setHouseType(quotePo.getHouseType());
        result.setHouseState(quotePo.getHouseStatus());
        result.setStoreId(quotePo.getStoreId());
        if (!StringUtils.isEmpty(quotePo.getHouseType())) {
            QuoteHouseEnum houseEnum = getQuoteRooms(quotePo.getHouseType());
            result.setHouseTypeName(houseEnum.getName());
        }
        // TODO  3D案例地址小区信息与design解耦，后期是否需要显示案例信息
        D3CaseBaseInfoRespBean caeBaseInfo = d3CaseFeignClient.getCaeBaseInfo(quotePo.getDesignId());
        if (caeBaseInfo == null) {
            logger.info("assetId:{},获取客户信息为空", quotePo.getDesignId());
//            throw new QuoteException(QuoteErrorEnum.CUSTOMER_INFORMATION_NOT_FOUND);
        } else {
            result.setCustomerName(caeBaseInfo.getCustomerName());
            result.setCustomerMobile(caeBaseInfo.getCustomerMobile());
            result.setProvinceId(caeBaseInfo.getProvinceId());
            result.setCityId(caeBaseInfo.getCityId());
            result.setDistrictId(caeBaseInfo.getDistrictId());
            result.setProvince(caeBaseInfo.getProvince());
            result.setCity(caeBaseInfo.getCity());
            result.setDistrict(caeBaseInfo.getDistrict());
            result.setCommunityName(caeBaseInfo.getCommunityName());
            result.setIsBindProject(caeBaseInfo.getIsBindProject());
            result.setMemberName(caeBaseInfo.getMemberName());
        }
        if (quotePo.getPackageId() != null) {
            result.setPackageId(quotePo.getPackageId().toString());
        }
        List<Package> packages = packageService.queryAllPackage();
        List<PackageInfoRespBean> list = new ArrayList<>();
        packages.forEach(pkg -> {
            PackageInfoRespBean respBean = new PackageInfoRespBean();
            respBean.setPackageId(pkg.getPackageId().toString());
            respBean.setPackageName(pkg.getPackageName());
            list.add(respBean);
        });
        result.setPackageInfos(list);
        return result;
    }

    private List<GetQuoteRoomDetailDto> getQuoteRoomDetail(Long quoteId) {
        List<QuoteRoomPo> quoteItemRoomDetail = listQuoteRoomPo(quoteId);
        List<GetQuoteRoomDetailDto> roomList = new ArrayList<>();
        for (QuoteRoomPo quoteRoomPo : quoteItemRoomDetail) {
            GetQuoteRoomDetailDto dto = new GetQuoteRoomDetailDto();
            QuoteRoomEnum quoteRoomEnum = QuoteRoomEnum.getById(quoteRoomPo.getRoomId());
            if (quoteRoomEnum != null) {
                dto.setRoomId(quoteRoomEnum.getId());
                dto.setRoomName(quoteRoomEnum.getName());
                dto.setRoomType(quoteRoomEnum.getType());
            }
            List<QuoteRoomMaterialPo> materialPos = listQuoteRoomMaterialPo(quoteRoomPo.getQuoteRoomId());
            //查询软装商品信息
            List<QuoteRoomTextilePo> homeTextile = listQuoteRoomTextilePo(quoteRoomPo.getQuoteRoomId());
            if (!CollectionUtils.isEmpty(homeTextile)) {
                homeTextile = getTextileByListIds(homeTextile);
            }
            dto.setHomeTextile(homeTextile);
            if (!CollectionUtils.isEmpty(materialPos)) {
                dto.setInnerMMList(materialPos.stream().filter(quoteRoomMaterialPo ->
                        QuoteItemTypeEnum.INNER_MM.getType().equals(quoteRoomMaterialPo.getQuoteItemType())).collect(Collectors.toList()));
                initMaterialBrandList(dto.getInnerMMList());
                dto.setOuterMMList(materialPos.stream().filter(quoteRoomMaterialPo ->
                        QuoteItemTypeEnum.OUTER_MM.getType().equals(quoteRoomMaterialPo.getQuoteItemType())).collect(Collectors.toList()));
                initMaterialBrandList(dto.getOuterMMList());
            }
            if (!CollectionUtils.isEmpty(quoteRoomPo.getConstructList())) {
                for (QuoteRoomConstructPo constructPo : quoteRoomPo.getConstructList()) {
                    initConstructPo(constructPo, quoteRoomPo.getQuoteRoomId());
                }
                dto.setInnerCIList(quoteRoomPo.getConstructList().stream().filter(quoteRoomConstructPo ->
                        QuoteItemTypeEnum.INNER_CI.getType().equals(quoteRoomConstructPo.getQuoteItemType())).collect(Collectors.toList()));
                dto.setOuterCIList(quoteRoomPo.getConstructList().stream().filter(quoteRoomConstructPo ->
                        QuoteItemTypeEnum.MANUAL_CI.getType().equals(quoteRoomConstructPo.getQuoteItemType())).collect(Collectors.toList()));
                dto.setOptionalCIList(quoteRoomPo.getConstructList().stream().filter(quoteRoomConstructPo ->
                        QuoteItemTypeEnum.GIFT_CI.getType().equals(quoteRoomConstructPo.getQuoteItemType())).collect(Collectors.toList()));
            }
            roomList.add(dto);
        }
        return roomList;
    }

    /**
     * 报价主要计算逻辑
     *
     * @param dto          响应实体
     * @param roomList     房间列表
     * @param quotePo      报价实体
     * @param bagOverPrice 礼包价格
     * @param packageBags  套餐基础价格
     * @return
     * @throws QuoteException
     */
    private GetQuoteSummaryDto calculateQuoteSummary(GetQuoteSummaryDto dto, List<GetQuoteRoomDetailDto> roomList, QuotePo quotePo, BigDecimal bagOverPrice, List<PackageBag> packageBags) throws QuoteException {
        List<GetQuoteRoomDetailDto> g = null;
        try {
            g = deepCopy(roomList);
        } catch (Exception e) {
            logger.info("deepCopy is error :{}", e);
        }
        dto.setRoomList(g);
        //套餐内主材项
        List<QuoteRoomMaterialPo> innerMaterialsItems = Lists.newArrayList();
        //超量的主材项
        List<QuoteRoomMaterialPo> overLimitMaterialsItems = Lists.newArrayList();
        //套餐外主材项
        List<QuoteRoomMaterialPo> outerMaterialsItems = Lists.newArrayList();
        //施工项
        List<QuoteRoomConstructPo> constructionItems = Lists.newArrayList();
        //超量的施工项
        List<QuoteRoomConstructPo> ciOverLimit = Lists.newArrayList();
        //大礼包的施工项
        List<QuoteRoomConstructPo> ciGifts = Lists.newArrayList();
        //自定义施工项
        List<QuoteRoomConstructPo> ciManual = Lists.newArrayList();
        //家纺家居列表
        List<QuoteRoomTextilePo> homeTextiles = Lists.newArrayList();
        //需要通过sku获取价格信息的product集合
        List<Product> productList = Lists.newArrayList();
        //循环QuoteRoomDetail 赋值以上List实体类
        for (GetQuoteRoomDetailDto detailDto : roomList) {
            if (!CollectionUtils.isEmpty(detailDto.getInnerMMList())) {
                for (QuoteRoomMaterialPo innerCiPo : detailDto.getInnerMMList()) {
                    innerCiPo.setQuoteRoomName(detailDto.getRoomName());
                    //如果limit大于0并且使用数量大于限制数量，则代表套餐内已超量
                    if (innerCiPo.getLimitQuantity().compareTo(BigDecimal.ZERO) > 0 && innerCiPo.getUsedQuantity().compareTo(innerCiPo.getLimitQuantity()) > 0) {
                        overLimitMaterialsItems.add(innerCiPo);
                    }
                    //主材带出的施工项
                    if (!CollectionUtils.isEmpty(innerCiPo.getConstructList())) {
                        for (QuoteRoomConstructPo constructPo : innerCiPo.getConstructList()) {
                            constructionItems.add(constructPo);
                        }
                    }
                }
                //赋值productList overLimitMaterialsItems
                getSelectedProductList(overLimitMaterialsItems, productList);
                detailDto.setInnerMMListOverPrice(calculateMaterialPrice(overLimitMaterialsItems, 1));
                //套餐内主材数量为0的不显示
                innerMaterialsItems.addAll(detailDto.getInnerMMList().stream().
                        filter(quoteRoomMaterialPo -> quoteRoomMaterialPo.getUsedQuantity().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList()));
                //赋值productList innerMaterialsItems
                getSelectedProductList(innerMaterialsItems, productList);
            }
            if (!CollectionUtils.isEmpty(detailDto.getOuterMMList())) {
                outerMaterialsItems.addAll(detailDto.getOuterMMList());
                //赋值productList
                getSelectedProductList(outerMaterialsItems, productList);
            }
            if (!CollectionUtils.isEmpty(detailDto.getInnerCIList())) {
                List<QuoteRoomConstructPo> constructPos = detailDto.getInnerCIList().stream().filter(quoteRoomConstructPo ->
                        quoteRoomConstructPo.getUsed() != null && quoteRoomConstructPo.getUsed().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
                detailDto.setInnerCIList(constructPos);
                constructionItems.addAll(constructPos);
            }
            if (!CollectionUtils.isEmpty(detailDto.getOptionalCIList())) {
                ciGifts.addAll(detailDto.getOptionalCIList());
            }
            if (!CollectionUtils.isEmpty(detailDto.getOuterCIList())) {
                ciManual.addAll(detailDto.getOuterCIList());
            }
            if (!CollectionUtils.isEmpty(detailDto.getHomeTextile())) {
                BigDecimal eachSoftPrice = calculateHouseTextilePrice(detailDto.getHomeTextile());
                detailDto.setEachSoftPrice(eachSoftPrice);
                homeTextiles.addAll(detailDto.getHomeTextile());
            }
        }
        if (!CollectionUtils.isEmpty(outerMaterialsItems)) {
            outerMaterialsItems.forEach(quoteRoomMaterialPo ->
                    {
                        if (!CollectionUtils.isEmpty(quoteRoomMaterialPo.getConstructList())) {
                            constructionItems.addAll(quoteRoomMaterialPo.getConstructList());
                        }
                    }
            );
        }
        List<QuoteRoomConstructPo> constructionItemResult = new ArrayList<>();
        //施工项去重，根据cid，limit相加，useQuantity相加，然后判断出超量的施工项
        if (!CollectionUtils.isEmpty(constructionItems)) {
            constructionItemResult = repeatedQuoteRoomConstruct(constructionItems);
            ciOverLimit.addAll(constructionItemResult.stream().filter(innerCiPo ->
                    innerCiPo.getLimit() != null && innerCiPo.getUsed() != null &&
                            innerCiPo.getLimit().compareTo(BigDecimal.ZERO) > 0 &&
                            innerCiPo.getUsed().compareTo(innerCiPo.getLimit()) > 0
            ).collect(Collectors.toList()));
        }
        dto.setInnerMaterialsItems(innerMaterialsItems);
        dto.setOverLimitMaterialsItems(overLimitMaterialsItems);
        dto.setOuterMaterialsItems(outerMaterialsItems);
        dto.setConstructionItems(constructionItemResult);
        ciOverLimit = repeatedQuoteRoomConstruct(ciOverLimit);
        dto.setHomeTextiles(homeTextiles);
        //计算超出施工项的价钱
        BigDecimal overCIPrice = calcCiPrice(ciOverLimit, 1);
        dto.setCiOverLimit(ciOverLimit);
        ciGifts = repeatedQuoteRoomConstruct(ciGifts);
        dto.setCiGifts(ciGifts);
        ciManual = repeatedQuoteRoomConstruct(ciManual);
        dto.setCiManual(ciManual);
        GetQuoteSummaryPriceRespBean price = new GetQuoteSummaryPriceRespBean();
        Package packageInfo = packageService.getById(quotePo.getPackageId());
        if (packageInfo != null) {
            dto.setQuoteName(packageInfo.getPackageName());
        }
        dto.setPackageId(quotePo.getPackageId());
        dto.setPackageVersionId(quotePo.getPackageVersionId());
        PackagePrice packagePrice = packageService.queryOnePackagePrice(quotePo.getPackageVersionId(), quotePo.getHouseType());
        if (packagePrice == null) {
            throw new QuoteException(QuoteErrorEnum.FIND_PACKAGE_PRICE_ERROR);
        }
        price.setTotalSize(quotePo.getInnerArea());
        price.setBasePrice(packagePrice.getBaseAmount());
        price.setOverPrice(packagePrice.getUnitPrice());
        if (quotePo.getInnerArea().compareTo(packagePrice.getBaseArea()) > 0) {
            price.setOverSize(quotePo.getInnerArea().subtract(packagePrice.getBaseArea()));
            price.setOverSizePrice(quotePo.getInnerArea().subtract(packagePrice.getBaseArea()).multiply(packagePrice.getUnitPrice()));
        }
        price.setOverCIPrice(overCIPrice);
        //计算大礼包是否超出价钱
        BigDecimal giftPrice = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(ciGifts)) {
            if (!CollectionUtils.isEmpty(packageBags)) {
                for (PackageBag packageBag : packageBags) {
                    if (packageBag.getAmount() == null) {
                        continue;
                    }
                    List<QuoteRoomConstructPo> collect = ciGifts.stream().filter(quoteRoomConstructPo -> packageBag.getBagId().equals(quoteRoomConstructPo.getBagId())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        for (QuoteRoomConstructPo quoteRoomConstructPo : collect) {
                            if (quoteRoomConstructPo.getUnitPrice() != null && quoteRoomConstructPo.getUsed() != null) {
                                giftPrice = giftPrice.add(quoteRoomConstructPo.getUnitPrice().multiply(quoteRoomConstructPo.getUsed()));
                            }
                        }
                        if (giftPrice.compareTo(packageBag.getAmount()) > 0) {
                            bagOverPrice = bagOverPrice.add(giftPrice).subtract(packageBag.getAmount());
                            price.setOverBagPrice(bagOverPrice);
                        }
                    }
                }
            }
        }
        logger.info("大礼包超出金额：{}", giftPrice);
        price.setOverCIPrice(price.getOverCIPrice().add(bagOverPrice));
        //计算套餐外施工项的价钱
        BigDecimal outerCIPrice = calcCiPrice(ciManual, 2);
        logger.info("套餐外施工项的价钱：{}", outerCIPrice);
        price.setOuterCIPrice(outerCIPrice);
        //   price.setHardAssemblyPrice(packagePrice.getBaseAmount().add(price.getOverSizePrice()).add(price.getOverCIPrice()).add(price.getOuterCIPrice()).add(price.getRemotePrice()));
        List<Product> products = updateProductPrice(productList);
        updateMaterialPoProductPrice(dto.getOverLimitMaterialsItems(), products);
        //计算套餐内-主材超量金额
        BigDecimal materialPrice = calculateMaterialPrice(dto.getOverLimitMaterialsItems(), 1);
        logger.info("套餐内-主材超量金额：{}", materialPrice);
        //计算套餐内主材金额
        updateMaterialPoProductPrice(dto.getInnerMaterialsItems(), products);
        BigDecimal inMaterialPrice = calculateMaterialPrice(dto.getInnerMaterialsItems(), 2);
        logger.info("套餐内主材金额：{}", inMaterialPrice);
        //计算套餐内施工项金额
        BigDecimal inCIPrice = calcCiPrice(dto.getConstructionItems(), 2);
        logger.info("套餐内施工项金额：{}", inCIPrice);
        //计算套餐外-主材金额
        updateMaterialPoProductPrice(dto.getOuterMaterialsItems(), products);
        BigDecimal outMaterialPrice = calculateMaterialPrice(dto.getOuterMaterialsItems(), 2);
        logger.info("套餐外-主材金额：{}", outMaterialPrice);
        //计算软装总价
        BigDecimal softPrice = calculateHouseTextilePrice(dto.getHomeTextiles());
        logger.info("软装总价：{}", softPrice);
        //硬装优惠价
        price.setHardAssemblyPrice(packagePrice.getBaseAmount().add(price.getOverSizePrice()).add(price.getOverCIPrice()).add(price.getOuterCIPrice())
                .add(price.getRemotePrice().add(outMaterialPrice).add(materialPrice)));
        price.setSoftPrice(softPrice);
        price.setTotalPrice(price.getHardAssemblyPrice().add(price.getSoftPrice()));
        price.setSquareMetrePrice(price.getTotalPrice().divide(price.getTotalSize(), 2, BigDecimal.ROUND_HALF_UP));
        price.setMaterialPrice(materialPrice);
        price.setOutMaterialPrice(outMaterialPrice);
        //计算硬装参考总价
        price.setReferenceHardAssemblyPrice(inMaterialPrice.add(inCIPrice).add(outMaterialPrice).add(outerCIPrice).add(price.getRemotePrice()).add(giftPrice));
        //计算软装参考总价
        price.setReferenceSoftPrice(softPrice);
        //计算参考总价 = 硬装参考总价 + 软装参考总价
        price.setReferenceTotalPrice(price.getReferenceHardAssemblyPrice().add(price.getReferenceSoftPrice()));
        //施工项总价
        BigDecimal totalCIPrice = inCIPrice.add(outerCIPrice).add(bagOverPrice);
        logger.info("施工项总价：{}", totalCIPrice);
        price.setTotalCIPrice(totalCIPrice);
        dto.setPrice(price);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        dto.setQuotationTime(formatter.format(new Date()));
        //因为之前用了addAll操作，下面需要进行更改一些展示的优化，先进行转换，清除内存引用
        dto = JsonUtil.toObject(JsonUtil.toJson(dto), GetQuoteSummaryDto.class);
        return dto;
    }

    /**
     * 筛选出需要计算价格的product列表
     *
     * @param materialPoList 主材列表
     * @param productList    单品列表
     */
    private void getSelectedProductList(List<QuoteRoomMaterialPo> materialPoList, List<Product> productList) {
        materialPoList.stream().forEach(materialPo -> {
            List<QuoteRoomMaterialBrandPo> collect = materialPo.getBrandList().stream().filter(QuoteRoomMaterialBrandPo::getSelected).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                collect.stream().forEach(brand -> {
                    List<Product> products = brand.getProduct().stream().filter(Product::getSelected).collect(Collectors.toList());
                    productList.addAll(products);
                });
            }
        });
    }

    private WriteResult updateQuote(QuoteDocumentPo quotePo, Optional<String> paramKey, Optional<Object> paramValue) {
        Map<String, Object> valuesMap = Maps.newHashMap();
        valuesMap.put(paramKey.get(), paramValue.get());
        Query basicQuery = MDQueryUtil.getDBObjectByValues(valuesMap);
        Update update = MDQueryUtil.getUpdateByValues(ClassUtil.getClassFieldAndValue(quotePo, Boolean.FALSE));
        WriteResult writeResult = mongoTemplate.updateMulti(basicQuery, update, QuoteDocumentPo.class);
        return writeResult;
    }


    private Properties getQuoteVersion(Long decorationTypeId, String provinceId) {
        Criteria criteria = new Criteria();
        criteria.and(propertiesUtils.getProperty("decorationType.decorationTypeId")).is(decorationTypeId);
        criteria.and(propertiesUtils.getProperty("quoteVersion.region")).is(provinceId);
        criteria.and(propertiesUtils.getProperty("quoteVersion.startTime")).lte(new Date());
        criteria.and(propertiesUtils.getProperty("quoteVersion.endTime")).gt(new Date());
        criteria.and(propertiesUtils.getProperty("common.status")).is(CommonStaticConst.Common.ENABLE_STATUS);
        QuoteVersionPo version = mongoTemplate.findOne(new Query(criteria), QuoteVersionPo.class);
        if (version == null) {
            return null;
        }
        Properties properties = new Properties();
        properties.put(propertiesUtils.getProperty("quoteVersion.quoteVersionCode"), version.getQuoteVersionCode());
        properties.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), version.getQuoteVersionId());
        return properties;
    }

    /**
     * 获取添加施工项列表
     *
     * @param quoteId
     * @return
     */
    public CommonRespBean getConstructList(Long quoteId, String constructName) throws QuoteException {
        List<QuerySupportConstructRespBean> resps = Lists.newArrayList();
        QuoteDocumentPo quote = this.getQuotePoById(quoteId);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("common.decorationCompany"), quote.getDecorationCompany());
        basicDBObject.put(propertiesUtils.getProperty("constructPrice.decorationTypeId"), quote.getDecorationTypeId());
        basicDBObject.put(propertiesUtils.getProperty("common.status"), CommonStaticConst.Common.ENABLE_STATUS);
        if(quote.getQuoteVersionId()==null){
            basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), new BasicDBObject("$eq",null));

        }else{
            basicDBObject.put(propertiesUtils.getProperty("quoteVersion.quoteVersionId"), quote.getQuoteVersionId());
        }
        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quote.getQuoteTypeId());

        List<ConstructPricePo> constructPricePos = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructPricePo.class);
        if (!CollectionUtils.isEmpty(constructPricePos)) {
            constructPricePos.forEach(p -> {
                QuerySupportConstructRespBean resp = new QuerySupportConstructRespBean();
                ConstructPo cachedConstruct = cacheUtil.getCachedIndiviConstruct(p.getConstructCode());
                if (cachedConstruct != null) {
                    resp.setConstructCode(p.getConstructCode());
                    resp.setConstructId(cachedConstruct.getConstructId());
                    resp.setConstructName(cachedConstruct.getConstructName());
                    resp.setDesc(cachedConstruct.getDesc());
                    resp.setAssitSpec(cachedConstruct.getAssitSpec());
                    resp.setUnitCode(p.getUnitCode());
                    resp.setCustomerPrice(p.getCustomerPrice());
                    resps.add(resp);
                } else {
                    logger.info("根据施工项编码:{}没有找到对应的缓存施工项", p.getConstructCode());
                }
            });
        } else {
            logger.info("无相应的维护好价格的施工项");
        }
        //说明不需要进行模糊匹配查询
        if (StringUtils.isEmpty(constructName)) {
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), resps);
        }
        List<QuerySupportConstructRespBean> collects = resps.stream().filter(p -> {
            boolean matches = p.getConstructName().contains(constructName);
            return matches;
        }).collect(Collectors.toList());
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "查询成功"), collects);

    }


    /**
     * 计算主材带出施工项数量
     *
     * @param source
     * @return
     */
    public CommonRespBean getConstructQuantity(ConstCalcRequest source) throws ParentException {
        List<ConstCalcResponse> resps = Lists.newArrayList();

        Properties properties = new Properties();
        properties.put(propertiesUtils.getProperty("quote.id"), source.getQuoteId());
        QuoteDocumentPo quote = findPoByUniqueKey(properties, QuoteDocumentPo.class);
        if (quote == null) {
            logger.info("系统无该报价{}", source.getQuoteId());
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "系统无该报价"));
        }

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindMaterials.categoryId"), source.getCategoryId());
        basicDBObject.put(propertiesUtils.getProperty("constructRelationShip.bindMaterials.isDefault"), CommonStaticConst.Construct.DEFAULT);
        basicDBObject.put(propertiesUtils.getProperty("quoteType.quoteTypeId"), quote.getQuoteTypeId());
        List<ConstructRelationship> constructRelationships = mongoTemplate.find(new BasicQuery(basicDBObject), ConstructRelationship.class);

        if (CollectionUtils.isEmpty(constructRelationships)) {
            logger.info("当前主材类目没有维护施工项带出关系");
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "当前主材类目没有维护施工项带出关系"));
        }

        CalculatorVarBean calculatorVarBean = this.buildCalcuationVarValue(quote);
        Optional<QuoteRoomDto> optionalRoom = quote.getRooms().stream().filter(p -> {
            if (source.getRoomName().equals(p.getRoomName()) && source.getRoomType().equals(p.getRoomType())) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).findFirst();
        if (!optionalRoom.isPresent()) {
            logger.info("系统无该报价，或者该报价没有具体空间信息");
            return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.DATA_NOT_EXISTING, "系统无该报价，或者该报价没有具体空间信息"));
        }

        QuoteRoomDto quoteRoomDto = optionalRoom.get();
        calculatorVarBean.setHouseArea(quote.getInnerArea());
        calculatorVarBean.setRoomArea(quoteRoomDto.getArea());
        calculatorVarBean.setRoomPerimeter(quoteRoomDto.getPerimeter());
        calculatorVarBean.setRoomHeight(quoteRoomDto.getHeight());

        logger.info("计算带出数量的对象数据:{}", JSON.toJSONString(calculatorVarBean));
        for (ConstructRelationship constShip : constructRelationships) {
            ConstCalcResponse resp = calcChangeUsed(constShip, calculatorVarBean, source.getNewUsed(), source.getOldUsed());
            if (resp != null) {
                logger.info("本次计算结果为:{}", JSON.toJSONString(resp));
            }
            resps.add(resp);
        }

        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "计算施工项数量成功"), resps);
    }

    /**
     * 计算施工项数量的变化值
     *
     * @param constShip
     * @param calculatorVarBean
     * @param newMaterialUsed
     * @param oldMaterialUsed
     * @return
     * @throws PackageException
     */
    private ConstCalcResponse calcChangeUsed(ConstructRelationship constShip, CalculatorVarBean calculatorVarBean, BigDecimal newMaterialUsed, BigDecimal oldMaterialUsed) throws PackageException {
        ConstCalcResponse resp = new ConstCalcResponse();
        resp.setConstructId(constShip.getConstructId());
        calculatorVarBean.setMaterialQuantity(newMaterialUsed);
        BigDecimal newUsed = constructService.calConstruct(constShip.getConstructId(),
                new CalObjectValueBean(calculatorVarBean));
        calculatorVarBean.setMaterialQuantity(oldMaterialUsed);
        BigDecimal oldUsed = constructService.calConstruct(constShip.getConstructId(),
                new CalObjectValueBean(calculatorVarBean));
        BigDecimal changeUsed = newUsed.subtract(oldUsed);
        resp.setChangeUsed(changeUsed);
        return resp;
    }

    /**
     * 获取装饰公司
     *
     * @param designId
     * @return
     */
    private String getDecorationCompanyFromConstructionPlatform(String designId) {
        if(StringUtils.isEmpty(textileThirdUrlConfig)){
            return null;
        }
        try {
            String url = textileThirdUrlConfig.getGetDecorationCompanyUrl().concat(designId);
            HttpResponse response = HttpSendUtil.sendHttpGet(url);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JSONObject context = JSON.parseObject(response.getContent());
                ProjectDecorationCompanyInfo data = JsonUtility.jsonToObject(context.get("data").toString(), ProjectDecorationCompanyInfo.class);
                return data.getDecorationCompany();
            }

        } catch (Exception e) {
            logger.error("[QuoteService:getDecorationCompanyFromConstructionPlatform]调用施工平台接口获取装饰公司出错,错误信息:{}", e);
            return null;
        }
        return null;
    }


    /**
     * 报价导出
     *
     * @param quoteId
     * @param exportType
     * @return
     */
    public CommonRespBean exportQuote(Long quoteId, ExportExcelEnums exportType) throws QuoteException {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(propertiesUtils.getProperty("quote.id"), quoteId);

        QuoteDocumentPo quote = mongoTemplate.findOne(new BasicQuery(basicDBObject), QuoteDocumentPo.class);
        if (quote == null) {
            logger.info("[QuoteService.exportQuote]:未找到:{} 对应报价信息!!!!!", quoteId);
            return null;
        }
        String templateFile = excelService.createExcel(quote, exportType);
        StreamingOutput output = null;
        File file = new File(templateFile);
        try {
            InputStream inputStream = new FileInputStream(file);
            output = out -> {
                int length;
                byte[] buffer = new byte[1024];
                while ((length = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            };
        } catch (IOException e) {
            logger.error("[QuoteService.exportQuote]:读取输出流出现异常:{}", e);
            throw new QuoteException("读取输出流出现异常");
        } finally {
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.SUCCESS, "下载完成"), output);
    }


}



