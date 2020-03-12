package com.juran.quote.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoBaoResponseBody {

    private String request_id;

    private TaoBaoResponseResult result;

}