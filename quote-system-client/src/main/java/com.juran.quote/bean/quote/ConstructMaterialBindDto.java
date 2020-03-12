package com.juran.quote.bean.quote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConstructMaterialBindDto {
    @ApiModelProperty(value = "主材类目id", required = true, example = "123456")
    private String materialCategoryId;

    @ApiModelProperty(value = "主材类目名称", required = true, example = "MM021")
    private String materialCategoryName;

    @ApiModelProperty(value = "是否默认带出", required = true, example = "1")
    private Integer isDefault;


}
