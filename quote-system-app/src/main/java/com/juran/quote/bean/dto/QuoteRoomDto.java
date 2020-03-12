package com.juran.quote.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@ApiModel
public class QuoteRoomDto {

    @ApiModelProperty(value = "房间类型", example = "2室1卫")
    private String roomType;

    @ApiModelProperty(value = "房间名称", example = "卫生间")
    private String roomName;

    @ApiModelProperty(value = "3D带出的房间ID", example = "15")
    private String roomID;

    @ApiModelProperty(value = "房间面积", example = "25.2")
    private BigDecimal area;

    @ApiModelProperty(value = "房间高度", example = "3.5")
    private BigDecimal height;

    @ApiModelProperty(value = "房间周长", example = "40.5")
    private BigDecimal perimeter;

    @ApiModelProperty(value = "该房间内报价", example = "9800")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "该房间软装总价", example = "4500")
    private BigDecimal softMaterialPrice;

    @ApiModelProperty(value = "该房间硬装主材总价", example = "4500")
    private BigDecimal hardMaterialPrice;

    @ApiModelProperty(value = "该房间施工项总价", example = "4500")
    private BigDecimal constructsPrice;

    @ApiModelProperty(value = "该空间内施工项")
    private List<BindingConstruct> constructList = Collections.EMPTY_LIST;

    @ApiModelProperty(value = "软装商品列表")
    private List<DecorationMaterial> softDecorationMaterials = Collections.EMPTY_LIST;

    @ApiModelProperty(value = "硬装商品列表")
    private List<DecorationMaterial> hardDecorationMaterials = Collections.EMPTY_LIST;

    @ApiModelProperty(value = "无效软装商品列表")
    private List<DecorationMaterial> invalidSoftDecorationMaterials = Collections.EMPTY_LIST;

    @ApiModelProperty(value = "无效硬装商品列表")
    private List<DecorationMaterial> invalidHardDecorationMaterials = Collections.EMPTY_LIST;
}
