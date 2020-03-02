package com.ecnu.paper.quotesystem.bean.dto.excel;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 08/12/2018 12:57 PM
 * @Description: 软装报价清单数据体
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class WholeHomeExcelDto extends QuoteBaseDto {

    /**
     * 参考总价：
     */
    private String totalPrice;

    /**
     * 平米单价：
     */
    private String unitPrice;

    /**
     * 材料总价：
     */
    private String materialTotalPrice;

    /**
     * 施工总价：
     */
    private String constructTotalPrice;

    /**
     * 软装总价：
     */
    private String softDecorationTotalPrice;

    /**
     * 硬装材料总价：
     */
    private String hardMaterialTotalPrice;
    /**
     * 硬装总价：
     */
    private String hardDecorationTotalPrice;

    /**
     * 其他费用：
     */
    private String otherPrice;
    /**
     * 硬装材料清单
     */
    private List<HardMaterialListDto> hardMaterialList = Lists.newArrayList();


    /**
     * 软装材料清单
     */
    private List<SoftDecorationListDto> softMaterialList = Lists.newArrayList();

    /**
     * 施工项清单
     */
    private List<ConstructListDto> constructList= Lists.newArrayList();
}
