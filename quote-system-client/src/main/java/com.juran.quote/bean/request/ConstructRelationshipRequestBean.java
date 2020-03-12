package com.juran.quote.bean.request;

import com.juran.quote.bean.quote.ConstructMaterialBindDto;
import com.juran.quote.bean.quote.ConstructRoomBindDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class ConstructRelationshipRequestBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = -6945063079828119105L;


    @ApiModelProperty(value = "施工项ID", required = true, example = "136")
    private Long constructId;

    @ApiModelProperty(value = "施工项编码", required = true, example = "136")
    private String constructCode;

    @ApiModelProperty(value = "报价类型", required = true, example = "1")
    private Long quoteTypeId;

    @ApiModelProperty(value = "装饰公司", required = true, example = "设计创客")
    private String  decorationCompany;

    @ApiModelProperty(value = "户型", required = false, example = "123")
    private Long houseTypeId;

    @ApiModelProperty(value = "带出类型", required = true, example = "room")
    private String type;

    @ApiModelProperty(value = "选择绑定主材", required = false)
    private List<ConstructMaterialBindDto> selectMaterialList;

    @ApiModelProperty(value = "移除绑定主材", required = false)
    private List<ConstructMaterialBindDto> removeMaterialList;

    @ApiModelProperty(value = "选择绑定空间", required = false)
    private List<ConstructRoomBindDto> selectRoomList;

    @ApiModelProperty(value = "移除绑定空间", required = false)
    private List<ConstructRoomBindDto> removeRoomList;

}
