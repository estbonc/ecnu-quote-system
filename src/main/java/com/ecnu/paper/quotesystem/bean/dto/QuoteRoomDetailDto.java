package com.ecnu.paper.quotesystem.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
public class QuoteRoomDetailDto implements Serializable {
    //房间类型
    private String roomType;

    //房间名称
    private String roomName;

    //3D带出的房间ID
    private String roomID;

    //面积
    private BigDecimal area;

    //房高
    private BigDecimal height;

    //周长
    private BigDecimal perimeter;

    // 该房间内报价
    private BigDecimal totalPrice;

    // 空间带出的施工项
    private List<BindingConstruct> autoConstructList = Collections.EMPTY_LIST;

    // 空间自定义的施工项
    private List<BindingConstruct> manualConstructList = Collections.EMPTY_LIST;

    // 软装商品
    private List<DecorationMaterial> softDecorationMaterials = Collections.EMPTY_LIST;

    // 硬装商品
    private  List<DecorationMaterial> hardDecorationMaterials = Collections.EMPTY_LIST;
}
