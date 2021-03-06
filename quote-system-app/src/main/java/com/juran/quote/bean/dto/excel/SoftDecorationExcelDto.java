package com.juran.quote.bean.dto.excel;

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
public class SoftDecorationExcelDto extends QuoteBaseDto{


    /**
     * 软装总价：
     */
    private String softDecorationPrice;


    /**
     * 软装材料清单
     */
    private List<SoftDecorationListDto> softMaterialList = Lists.newArrayList();







}
