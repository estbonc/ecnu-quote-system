package com.juran.quote.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoBaoRelateItem {

    private String code;
    private TaoBaoResponseData data;
    private Boolean success;
}