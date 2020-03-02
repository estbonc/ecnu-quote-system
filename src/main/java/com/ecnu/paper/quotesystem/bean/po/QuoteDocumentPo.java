package com.ecnu.paper.quotesystem.bean.po;

import com.juran.quote.bean.dto.ChargeTypeDto;
import com.juran.quote.bean.dto.QuoteCustomerDto;
import com.juran.quote.bean.dto.QuoteResult;
import com.juran.quote.bean.dto.QuoteRoomDto;
import com.juran.quote.bean.quote.BomRoomDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Document(collection = "quoteDocument")
@Data
public class QuoteDocumentPo extends BasePo{

    // 报价ID

    private Long quoteId;

    // 3D-设计方案ID
    private String designId;

    //设计师姓名
    private String designerName;

    // 项目ID
    private Long projectId;

    // 套内面积
    private BigDecimal innerArea;

    // 报价包含的房间
    private List<QuoteRoomDto> rooms;

    // 3D 设计房间列表
    private List<BomRoomDto> designRooms;

    // 报价价格总表
    private QuoteResult quoteResult;

    // 额外收费
    private List<ChargeTypeDto> extraCharges = Collections.EMPTY_LIST;

    // 报价所属装饰公司
    private String decorationCompany;

    // 报价类型id
    private Long quoteTypeId;

    // 报价类型
    private String quoteType;

    // 装修类型id
    private Long decorationTypeId;

    // 装修类型
    private String decorationType;

    //报价版本id
    private Long quoteVersionId;

    //报价版本code
    private String quoteVersionCode;

    // 户型id
    private Long houseTypeId;

    // 房型
    private String houseTypeCode;

    //房型名称
    private String houseType;

    //客户信息
    private QuoteCustomerDto customerInfo;

    //报价状态：1：开始报价，2：待提交 3：已完成
    private Integer step;
}
