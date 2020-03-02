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
public class MaterialExcelDto extends QuoteBaseDto{



    /**
     * 材料总价：
     */
    private String materialTotalPrice;


    /**
     * 软装材料总价：
     */
    private String softDecorationTotalPrice;

    /**
     * 硬装材料总价：
     */
    private String hardMaterialTotalPrice;


    /**
     * 硬装材料清单
     */
    private List<HardMaterialListDto> hardMaterialList = Lists.newArrayList();


    /**
     * 软装材料清单
     */
    private List<SoftDecorationListDto> softMaterialList = Lists.newArrayList();
}
