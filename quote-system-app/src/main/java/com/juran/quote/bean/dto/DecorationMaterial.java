package com.juran.quote.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@ApiModel
public class DecorationMaterial {

    @ApiModelProperty(value = "3D带出的类目Id", example = "4c28f7eb-b2dc-4129-80e1-edcb11acb485")
    private String categoryId;

    @ApiModelProperty(value = "主材类目名称", example = "台盆")
    private String materialName;

    @ApiModelProperty(value = "主材类型，主要针对套餐，IN:套餐内 OUT：套餐外", example = "IN")
    private String materialType;

    @ApiModelProperty(value = "阿里itemId", example = "123456")
    private String aliItemId;

    @ApiModelProperty(value = "阿里skuId", example = "123456")
    private String aliSkuId;

    @ApiModelProperty(value = "居然sku", example = "504681282754107")
    private String juranSku;

    @ApiModelProperty(value = "阿里itemType", example = "厨卫用品")
    private String aliItemType;

    @ApiModelProperty(value = "单位", example = "米")
    private String unit;

    @ApiModelProperty(value = "限量值", example = "10")
    private BigDecimal limit;

    @ApiModelProperty(value = "使用量", example = "10")
    private BigDecimal usedQuantity = BigDecimal.ZERO;

    @ApiModelProperty(value = "品牌名称", example = "大地")
    private String brand;

    @ApiModelProperty(value = "商品型号", example = "大地520")
    private String model;
	@ApiModelProperty(value = "商品规格", example = "大地520")
	private String spec;
    @ApiModelProperty(value = "阿里modelId", example = "504681282754107")
    private String aliModelId;

    @ApiModelProperty(value = "单价", example = "250")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "图片url", example = "https://jr-prod-pim-products.oss-cn-beijing.aliyuncs.com/i/ef223247-429e-43b4-bd72-ba6f0ae3c1f6/resized/iso_w160_h160.jpg")
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecorationMaterial)) return false;
        DecorationMaterial that = (DecorationMaterial) o;
        return Objects.equals(materialName, that.materialName) &&
                Objects.equals(juranSku, that.juranSku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId,
                materialName,
                materialType,
                aliItemId,
                aliSkuId,
                juranSku,
                aliItemType,
                unit,
                limit,
                usedQuantity,
                brand,
                model,
                aliModelId,
                unitPrice);
    }
}
