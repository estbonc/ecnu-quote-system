package com.ecnu.paper.quotesystem.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 23/10/2018 4:46 PM
 * @Description: 主材查询返回vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
@ApiModel
public class MaterialRelationShipRespBean {

    @ApiModelProperty(value = "主材类目id", required = true, example = "123456")
    private String materialCategoryId;

    @ApiModelProperty(value = "主材类目名称", required = true, example = "MM021")
    private String materialCategoryName;

    @ApiModelProperty(value = "是否默认带出", required = true, example = "1")
    private Integer isDefault;

    @ApiModelProperty(value = "是否选中", required = true, example = "1")
    private  Integer hasBind;

}
