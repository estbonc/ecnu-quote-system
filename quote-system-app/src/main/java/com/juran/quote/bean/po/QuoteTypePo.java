package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "quoteType")
public class QuoteTypePo extends BasePo {
    private Long quoteTypeId;
    private String decorationCompany;
    private Long decorationTypeId;
    private String name;
    private String code;
    private String description;
    private Integer status;
}
