package com.juran.quote.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoBaoResponseItemData {

    private Long itemId;

    private Long skuId;

    private Long originPrice;

    private Long zkPrice;

    private Long shopId;

    private String shopName;

    private String title;

    private String mainPic;

    private int itemType;

    private int sales;

    private HashMap<String, Object> extra;  //city , location

}