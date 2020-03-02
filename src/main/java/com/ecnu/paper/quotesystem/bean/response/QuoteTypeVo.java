package com.ecnu.paper.quotesystem.bean.response;

import com.juran.quote.bean.request.BaseBean;
import lombok.Data;

@Data
public class QuoteTypeVo extends BaseBean {
    private Long quoteTypeId;
    private String decorationCompany;
    private String decorationType;
    private Long decorationTypeId;
    private String name;
    private String code;
    private String description;
    private Integer status;
}
