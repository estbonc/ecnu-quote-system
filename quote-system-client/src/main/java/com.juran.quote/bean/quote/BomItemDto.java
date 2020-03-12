package com.juran.quote.bean.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 3D工具-bom清单明细
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BomItemDto implements Serializable {
    private static final long serialVersionUID = 8629172762234467073L;
    /**
     * item的类型 套餐内 套餐外 主材 施工项
     */
    private String type;
    private String name;
    private String brand;
    private String sku;
    private BigDecimal quantity;
    private String price;
    private String unit;
    private String roomID;
    private String categoryId;
    private String modelId;
    private String imageUrl;
    /**
     * 额外参数
     */
    private AdditionalDto addtional;

    public BomItemDto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BomItemDto)) return false;
        BomItemDto that = (BomItemDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, name, brand, sku, quantity, price, unit, roomID, categoryId, modelId, imageUrl, addtional);
    }
}
