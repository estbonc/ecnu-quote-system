package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreRespBean implements Serializable {
    private static final long serialVersionUID = -1328770519982826688L;
    /**
     * 门店编码：LW01
     */
    private String code;

    /**
     * 门店名称：居然装饰北京乐屋北四环店一部
     */
    private String name;

}
