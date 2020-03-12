package com.juran.quote.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoBaoResponseData {

    Long modelId;

    String jid;

    public Boolean success;

    List<TaoBaoResponseItemData> items;
}