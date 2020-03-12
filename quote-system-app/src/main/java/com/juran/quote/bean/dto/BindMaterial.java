package com.juran.quote.bean.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/8/8 14:30
 * @description 施工项选中的主材实体
 */
@Data
public class BindMaterial {

    /**
     * 材料类目id
     */
    private String materialCategoryId;

    /**
     * 材料类目名称
     */
    private String materialCategoryName;

    /**
     * 是否默认
     */
    private Integer isDefault;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindMaterial that = (BindMaterial) o;
        return Objects.equals(materialCategoryId, that.materialCategoryId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(materialCategoryId, materialCategoryName, isDefault);
    }
}
