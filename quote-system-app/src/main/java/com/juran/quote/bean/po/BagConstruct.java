package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@Document(collection = "bagConstruct")
public class BagConstruct {

    @Indexed
    private Long constructId;

    @Indexed
    private Long bagId;

    private BigDecimal unitPrice;
}
