package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "calculatorExpression")
public class CalculatorExpression {
    @Id
    private String id;

    private Long expressionId;

    private Long constructId;

    private String expression;
}
