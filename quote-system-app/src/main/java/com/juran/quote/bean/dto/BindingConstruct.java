package com.juran.quote.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@ApiModel
public class BindingConstruct {

    @ApiModelProperty(value = "客户报价", example = "250")
    private BigDecimal customerPrice;

    @ApiModelProperty(value = "工长报价", example = "250")
    private BigDecimal foremanPrice;

    @ApiModelProperty(value = "单价", example = "250")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "施工项id", example = "12345678")
    private Long constructId;

    @ApiModelProperty(value = "施工项编码", example = "安装01")
    private String constructCode;

    @ApiModelProperty(value = "施工项名称", example = "安装01，安装地板踢脚线")
    private String constructName;

    @ApiModelProperty(value = "使用量", example = "40")
    private BigDecimal usedQuantity;

    @ApiModelProperty(value = "限量值", example = "0")
    private BigDecimal limit;

    @ApiModelProperty(value = "工艺材料简介")
    private String desc;

    @ApiModelProperty(value = "辅料名称规格")
    private String assitSpec;

    @ApiModelProperty(value = "单位")
    private String unitCode;

    @ApiModelProperty(value = "单位名称")
    private String unitCodeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingConstruct that = (BindingConstruct) o;
        return Objects.equals(constructId, that.constructId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(customerPrice, foremanPrice, unitPrice, constructId, constructCode, constructName, usedQuantity, limit, desc, assitSpec);
    }
}
