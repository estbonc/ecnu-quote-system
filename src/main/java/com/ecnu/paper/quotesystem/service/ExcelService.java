package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.quote.bean.dto.QuoteCustomerDto;
import com.juran.quote.bean.dto.QuoteResult;
import com.juran.quote.bean.dto.QuoteRoomDto;
import com.juran.quote.bean.dto.excel.*;
import com.juran.quote.bean.enums.ExportExcelEnums;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.po.QuoteDocumentPo;
import com.juran.quote.utils.FreemarkerUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiongtao
 * @Date: 04/12/2018 4:18 PM
 * @Description: 组装excel相关数据的service
 * @Email: xiongtao@juran.com.cn
 */
@Service
public class ExcelService extends BaseService {


    /**
     * 根据输入的exportType路由生成对应的excel文件
     *
     * @param quote
     * @param exportType
     * @return
     * @throws QuoteException
     */
    public String createExcel(QuoteDocumentPo quote, ExportExcelEnums exportType) throws QuoteException {
        String tempFileName = propertiesUtils.getProperty("common.dir.tmp") + File.separator + exportType.getName() + quote.getQuoteId() + ".xlsx";
        try {
            logger.info("[ExcelService.createExcel]:开始组装报价导出excel数据===================>");
            String templateFile = exportType.getTemplate();
            // 转义掉word的非法字符
            quote = replaceIllegalCharacter(quote);
            Map<String, Object> retMap = Maps.newHashMap();
            //先组装基础数据
            switch (ExportExcelEnums.getEnumByCode(exportType.getCode())) {
                case WHOLEHOME:
                    buildWholeHomeExcel(quote, retMap);
                    break;
                case CONSTRUCT:
                    buildConstructExcel(quote, retMap);
                    break;
                case MATERIAL:
                    buildMaterialExcel(quote, retMap);
                    break;
                case HARD_DECORATION:
                    buildHardMaterialExcel(quote, retMap);
                    break;
                case SOFT_DECORATION:
                    buildSoftMaterialExcel(quote, retMap);
                    break;
            }
            Writer tempFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(tempFileName))));
            FreemarkerUtil.exportToExcel(propertiesUtils.getProperty("common.dir.template"), retMap, templateFile, tempFile);
        } catch (Exception e) {
            logger.error("[ExcelService.createExcel]生成excel异常:{}", e);
            throw new QuoteException("生成excel异常");
        }
        return tempFileName;

    }

    /**
     * 替换非法字符
     * @param quote
     * @return
     */
    private QuoteDocumentPo replaceIllegalCharacter(QuoteDocumentPo quote) {
        String quoteString = JSON.toJSONString(quote);
        quoteString=quoteString.replaceAll("&", "&amp;");
        quoteString=quoteString.replaceAll("<", "&lt;");
        quoteString=quoteString.replaceAll(">", "&gt;");
        QuoteDocumentPo quoteDocumentPo = JSON.parseObject(quoteString, QuoteDocumentPo.class);
        return quoteDocumentPo;
    }

    /**
     * 软装报价excel
     *
     * @param
     * @param quote
     * @return
     */
    private void buildSoftMaterialExcel(QuoteDocumentPo quote, Map<String, Object> retMap) {
        SoftDecorationExcelDto softDecorationExcelDto = null;
        softDecorationExcelDto = new SoftDecorationExcelDto();
        //组装公共数据
        buildComonData(softDecorationExcelDto, quote);

        QuoteResult quoteResult = quote.getQuoteResult();
        if (quoteResult != null) {
            softDecorationExcelDto.setSoftDecorationPrice(bigDecimalToString(quoteResult.getSoftDecorationPrice()));
        }
        List<SoftDecorationListDto> data = Lists.newArrayList();
        buildSoftDecoration(data, quote);
        softDecorationExcelDto.setSoftMaterialList(data);
        retMap.put("data", softDecorationExcelDto);

    }

    /**
     * 组装施工项报价excel
     *
     * @param
     * @param quote
     * @return
     */
    private void buildConstructExcel(QuoteDocumentPo quote, Map<String, Object> retMap) {
        ConstructExcelDto constructExcelDto = new ConstructExcelDto();
        //组装公共数据
        buildComonData(constructExcelDto, quote);
        QuoteResult quoteResult = quote.getQuoteResult();
        if (quoteResult != null) {
            constructExcelDto.setConstructTotalPrice(bigDecimalToString(quoteResult.getConstructionTotalPrice()));
            constructExcelDto.setUnitPrice(bigDecimalToString(quoteResult.getUnitPrice()));
        }
        List<ConstructListDto> data = Lists.newArrayList();
        buildConstructList(data, quote);
        constructExcelDto.setConstructList(data);
        retMap.put("data", constructExcelDto);


    }

    /**
     * 材料报价excel
     *
     * @param
     * @param quote
     * @return
     */
    private void buildMaterialExcel(QuoteDocumentPo quote, Map<String, Object> retMap) {
        MaterialExcelDto materialExcelDto = new MaterialExcelDto();
        //组装公共数据
        buildComonData(materialExcelDto, quote);
        QuoteResult quoteResult = quote.getQuoteResult();
        if (quoteResult != null) {
            materialExcelDto.setMaterialTotalPrice(bigDecimalToString(quoteResult.getMaterialTotalPrice()));
            materialExcelDto.setSoftDecorationTotalPrice(bigDecimalToString(quoteResult.getSoftDecorationPrice()));
            materialExcelDto.setHardMaterialTotalPrice(bigDecimalToString(quoteResult.getMaterialTotalPrice().subtract(quoteResult.getSoftDecorationPrice())));
        }

        List<HardMaterialListDto> hardMaterialData = Lists.newArrayList();
        List<SoftDecorationListDto> softMaterialData = Lists.newArrayList();

        buildHardMaterialList(hardMaterialData, quote);
        buildSoftDecoration(softMaterialData, quote);
        materialExcelDto.setHardMaterialList(hardMaterialData);
        materialExcelDto.setSoftMaterialList(softMaterialData);

        retMap.put("data", materialExcelDto);


    }

    /**
     * 组装全屋定制excel
     *
     * @param
     * @param quote
     * @return
     */
    private void buildWholeHomeExcel(QuoteDocumentPo quote, Map<String, Object> retMap) {
        WholeHomeExcelDto wholeHomeExcelDto = new WholeHomeExcelDto();

        //组装公共数据
        buildComonData(wholeHomeExcelDto, quote);
        QuoteResult quoteResult = quote.getQuoteResult();
        if (quoteResult != null) {
            wholeHomeExcelDto.setTotalPrice(bigDecimalToString(quoteResult.getTotalPrice()));
            wholeHomeExcelDto.setUnitPrice(bigDecimalToString(quoteResult.getUnitPrice()));
            wholeHomeExcelDto.setMaterialTotalPrice(bigDecimalToString(quoteResult.getMaterialTotalPrice()));
            wholeHomeExcelDto.setConstructTotalPrice(bigDecimalToString(quoteResult.getConstructionTotalPrice()));
            wholeHomeExcelDto.setSoftDecorationTotalPrice(bigDecimalToString(quoteResult.getSoftDecorationPrice()));
            wholeHomeExcelDto.setHardMaterialTotalPrice(bigDecimalToString(quoteResult.getMaterialTotalPrice().subtract(quoteResult.getSoftDecorationPrice())));
            wholeHomeExcelDto.setOtherPrice(bigDecimalToString(quoteResult.getExtraPrice()));
            wholeHomeExcelDto.setHardDecorationTotalPrice(bigDecimalToString(quoteResult.getHardDecorationPrice()));

        }

        List<HardMaterialListDto> hardMaterialData = Lists.newArrayList();
        List<SoftDecorationListDto> softMaterialData = Lists.newArrayList();
        List<ConstructListDto> constructListData = Lists.newArrayList();


        buildHardMaterialList(hardMaterialData, quote);
        buildSoftDecoration(softMaterialData, quote);
        buildConstructList(constructListData, quote);

        wholeHomeExcelDto.setHardMaterialList(hardMaterialData);
        wholeHomeExcelDto.setSoftMaterialList(softMaterialData);
        wholeHomeExcelDto.setConstructList(constructListData);

        retMap.put("data", wholeHomeExcelDto);


    }


    /**
     * 硬装报价excel
     *
     * @param
     * @param quote
     * @return
     */
    private void buildHardMaterialExcel(QuoteDocumentPo quote, Map<String, Object> retMap) {
        HardDecorationExcelDto hardDecorationExcelDto = new HardDecorationExcelDto();

        //组装公共数据
        buildComonData(hardDecorationExcelDto, quote);
        QuoteResult quoteResult = quote.getQuoteResult();
        if (quoteResult != null) {
            hardDecorationExcelDto.setHardDecorationTotalPrice(bigDecimalToString(quoteResult.getHardDecorationPrice()));
            hardDecorationExcelDto.setHardMaterialTotalPrice(bigDecimalToString(quoteResult.getMaterialTotalPrice().subtract(quoteResult.getSoftDecorationPrice())));
            hardDecorationExcelDto.setConstructTotalPrice(bigDecimalToString(quoteResult.getConstructionTotalPrice()));
            hardDecorationExcelDto.setUnitPrice(bigDecimalToString(quoteResult.getUnitPrice()));
        }
        List<HardMaterialListDto> hardMaterialData = Lists.newArrayList();
        List<ConstructListDto> constructData = Lists.newArrayList();

        buildHardMaterialList(hardMaterialData, quote);
        buildConstructList(constructData, quote);

        hardDecorationExcelDto.setHardMaterialList(hardMaterialData);
        hardDecorationExcelDto.setConstructList(constructData);

        retMap.put("data", hardDecorationExcelDto);


    }

    /**
     * 组装硬装材料数据
     *
     * @param hardMaterialData
     * @param quote
     */
    private void buildHardMaterialList(List<HardMaterialListDto> hardMaterialData, QuoteDocumentPo quote) {
        List<QuoteRoomDto> rooms = quote.getRooms();
        if (!CollectionUtils.isEmpty(rooms)) {
            rooms.forEach(p -> {
                //合并有效硬装和无效硬装
                p.getHardDecorationMaterials().addAll(p.getInvalidHardDecorationMaterials());
                p.getHardDecorationMaterials().forEach(q -> {
                    HardMaterialListDto hardMaterialListDto = new HardMaterialListDto();
                    hardMaterialListDto.setRoomName(p.getRoomName());
                    hardMaterialListDto.setProductName(q.getMaterialName());
                    hardMaterialListDto.setBrandName(q.getBrand());
                    hardMaterialListDto.setModel(q.getModel());
                    hardMaterialListDto.setSpec(q.getSpec());
                    hardMaterialListDto.setUnitPrice(bigDecimalToString(q.getUnitPrice()));
                    hardMaterialListDto.setUseQuantity(bigDecimalToString(q.getUsedQuantity()));
                    hardMaterialListDto.setUsePrice(bigDecimalToString(calcSubPrice(q.getUnitPrice(), q.getUsedQuantity())));
                    hardMaterialData.add(hardMaterialListDto);
                });
            });
        }


    }


    /**
     * 组装施工项列表数据
     *
     * @param data
     * @param quote
     */
    private void buildConstructList(List<ConstructListDto> data, QuoteDocumentPo quote) {
        List<QuoteRoomDto> rooms = quote.getRooms();
        if (!CollectionUtils.isEmpty(rooms)) {

            rooms.forEach(p -> {
                p.getConstructList().forEach(q -> {
                    ConstructListDto constructListDto = new ConstructListDto();
                    constructListDto.setRoomName(p.getRoomName());
                    constructListDto.setConstructCode(q.getConstructCode());
                    constructListDto.setConstructName(q.getConstructName());
                    constructListDto.setDesc(q.getDesc());
                    constructListDto.setAssitSpec(q.getAssitSpec());
                    constructListDto.setUnitPrice(bigDecimalToString(q.getCustomerPrice()));
                    constructListDto.setUsedQuantity(bigDecimalToString(q.getUsedQuantity()));
                    constructListDto.setUsePrice(bigDecimalToString(calcSubPrice(q.getCustomerPrice(), q.getUsedQuantity())));
                    data.add(constructListDto);
                });
            });
        }


    }


    /**
     * 组装软装列表数据
     *
     * @param data
     * @param quote
     */
    private void buildSoftDecoration(List<SoftDecorationListDto> data, QuoteDocumentPo quote) {
        List<QuoteRoomDto> rooms = quote.getRooms();
        if (!CollectionUtils.isEmpty(rooms)) {

            rooms.forEach(p -> {
                //合并有效软装和无效软装
                p.getSoftDecorationMaterials().addAll(p.getInvalidSoftDecorationMaterials());
                p.getSoftDecorationMaterials().forEach(q -> {
                    SoftDecorationListDto softDecorationListDto = new SoftDecorationListDto();
                    softDecorationListDto.setRoomName(p.getRoomName());
                    softDecorationListDto.setProductName(q.getMaterialName());
                    softDecorationListDto.setBrandName(q.getBrand());
                    softDecorationListDto.setUnitPrice(bigDecimalToString(q.getUnitPrice()));
                    softDecorationListDto.setUseQuantity(bigDecimalToString(q.getUsedQuantity()));
                    softDecorationListDto.setUsePrice(bigDecimalToString(calcSubPrice(q.getUnitPrice(), q.getUsedQuantity())));
                    data.add(softDecorationListDto);
                });
            });
        }
    }

    /**
     * 组装公共数据
     */
    private void buildComonData(QuoteBaseDto quoteBaseDto, QuoteDocumentPo quote) {
        quoteBaseDto.setDecorationCompany(quote.getDecorationCompany());
        QuoteCustomerDto customerInfo = quote.getCustomerInfo();
        if (customerInfo != null) {
            quoteBaseDto.setCustomerName(customerInfo.getCustomerName());
            quoteBaseDto.setCustomerMobile(customerInfo.getCustomerMobile());
            quoteBaseDto.setAddress(customerInfo.getAddress());
        }
        quoteBaseDto.setDesignerName(quote.getDesignerName());
        quoteBaseDto.setArea(bigDecimalToString(quote.getInnerArea()));
        quoteBaseDto.setHouseType(quote.getHouseType());
        quoteBaseDto.setQuoteTime(new SimpleDateFormat("yyyy-MM-dd").format(quote.getCreateTime()));
        quoteBaseDto.setDecorationType(quote.getDecorationType());
        quoteBaseDto.setQuoteType(quote.getQuoteType());
    }


    /**
     * 计算商品单价
     */
    private BigDecimal calcSubPrice(BigDecimal unitPrice, BigDecimal useQuantity) {
        if (unitPrice != null && useQuantity != null) {
            return unitPrice.multiply(useQuantity).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 防止bigdecimal转化string报空指针
     *
     * @param bigDecimal
     * @return
     */
    private String bigDecimalToString(BigDecimal bigDecimal) {
        if (bigDecimal != null) {
            return bigDecimal.toPlainString();
        }
        return null;
    }

}
