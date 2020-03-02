package com.ecnu.paper.quotesystem.bean.mini;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MiniProductRespBean implements Serializable {

    private String vendor;
    private String name;
    private String imageResized;
    private String sku;
    private List<MiniProductSkinRespBean> variations;
}
