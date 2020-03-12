package com.juran.quote.bean.request;

import com.google.common.collect.Lists;
import com.juran.quote.bean.quote.SelectedConstructDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
public class MaterialRequestBean {
    @QueryParam("id")
    @ApiModelProperty(notes = "主材ID", required = false, example = "999999")
    private Long materialId;
    @QueryParam("packageRoomId")
    @ApiModelProperty(notes = "套餐空间ID", required = true, example = "999999")
    private Long packageRoomId;
    @QueryParam("主材名称")
    @ApiModelProperty(notes = "主材名称", required = true, example = "门")
    private String materialName;
    @QueryParam("主材编码")
    @ApiModelProperty(notes = "主材编码", required = true, example = "MM01")
    private String materialCode;
    @QueryParam("主材单位编码")
    @ApiModelProperty(notes = "主材单位编码", required = true, example = "M")
    private String materialUnitCode;
    @QueryParam("主材单位名称")
    @ApiModelProperty(notes = "主材单位", required = true, example = "米")
    private String materialUnitName;
    @QueryParam("主材限制数量")
    @ApiModelProperty(notes = "主材套餐内限量值", required = true, example = "1")
    private BigDecimal materialLimit;
    @QueryParam("主材品牌")
    @ApiModelProperty(notes = "主材品牌", required = true, example = "龙鼎")
    private String brandName;
    @QueryParam("品牌编号")
    @ApiModelProperty(notes = "品牌编号", required = true, example = "P01")
    private String brandCode;
    @QueryParam("商品编号")
    @ApiModelProperty(notes = "商品编码", required = true, example = "PAC0001")
    private String productCode;
    @QueryParam("productName")
    @ApiModelProperty(notes = "商品名称", required = true, example = "实木烤漆木门")
    private String productName;
    @QueryParam("商品规格")
    @ApiModelProperty(notes = "商品规格", required = true, example = "10*20cm")
    private String productSpec;
    @QueryParam("商品型号")
    @ApiModelProperty(notes = "商品型号", required = true, example = "10*20cm")
    private String productModel;
    @QueryParam("商品价格")
    @ApiModelProperty(notes = "商品价格", required = true, example = "100")
    private BigDecimal productPrice;
    @QueryParam("是否是超市商品")
    @ApiModelProperty(notes = "是否超市商品", required = true, example = "1")
    private String coho;
    @QueryParam("对应主数据中的sku")
    @ApiModelProperty(notes = "商品SKU", required = true, example = "00000122113")
    private String mdmSku;
    @QueryParam("带出施工项")
    @ApiModelProperty(notes = "带出施工项", required = false, example = "")
    private List<SelectedConstructDTO> selectedConstruct = Lists.newArrayList();
}
