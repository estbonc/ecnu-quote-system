package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "decorationType")
public class DecorationTypePo extends BasePo {
    private Long decorationTypeId;
    private String name;
    private String description;
    private String decorationCompany;
    private Integer status;
}
