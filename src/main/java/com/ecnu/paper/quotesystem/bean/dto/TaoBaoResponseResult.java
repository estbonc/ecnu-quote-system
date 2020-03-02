package com.ecnu.paper.quotesystem.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoBaoResponseResult {

    private String headers;

    private String model;

    private Boolean success;

}