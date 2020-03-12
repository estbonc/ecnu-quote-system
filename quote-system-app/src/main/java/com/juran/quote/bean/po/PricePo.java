package com.juran.quote.bean.po;

import com.juran.mdm.price.client.bean.response.entity.UnitPrice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@ApiModel(
        description = "价格响应"
)
@Data
@Document(collection = "material_price")
@Deprecated
public class PricePo implements Serializable {
    private static final long serialVersionUID = -5185364865076391426L;
    private static String RESOURCE = "Price";
    @ApiModelProperty("产品ID")
    private String productId;
    @ApiModelProperty("资源ID")
    private String resourceId;
    @ApiModelProperty("资源名称")
    private String resourceName;
    @ApiModelProperty("单位价格")
    private List<UnitPrice> prices;

    public PricePo() {
    }
}
