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
public class ConstructExcelDto extends QuoteBaseDto{




    /**
     * 平米单价：
     */
    private String unitPrice;


    /**
     * 施工总价：
     */
    private String constructTotalPrice;



    /**
     * 软装总价：
     */
    private List<ConstructListDto> constructList = Lists.newArrayList();




}
