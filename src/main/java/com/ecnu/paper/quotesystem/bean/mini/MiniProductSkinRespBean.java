package com.ecnu.paper.quotesystem.bean.mini;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiniProductSkinRespBean implements Serializable {

    private String name;
    private String imageResized;
    private String sku;
}
