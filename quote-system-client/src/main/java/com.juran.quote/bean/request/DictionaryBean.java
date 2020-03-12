package com.juran.quote.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 10/09/2018 6:27 PM
 * @Description: 字典vo
 * @Email: xiongtao@juran.com.cn
 */
@ApiModel("字典vo")
@Data
public class DictionaryBean {


    @ApiModelProperty(value = "字段分类", required = true, example = "A")
    private String type;

    @ApiModelProperty(value = "字段编码", required = true, example = "900001")
    private String code;

    @ApiModelProperty(value = "字段名称", required = true, example = "标准施工项")
    private String name;

    @ApiModelProperty(value = "字段描述", required = true, example = "这是一个标准施工项")
    private String description;

}
