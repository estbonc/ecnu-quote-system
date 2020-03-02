package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 门店信息
 */
@Data
@Document(collection = "store")
public class StorePo  implements Serializable {

    private static final long serialVersionUID = -6219858604126526963L;
    /**
     * 门店编码：LW01
     */
    private String code;

    /**
     * 门店名称：居然装饰北京乐屋北四环店一部
     */
    private String name;
    /**
     * 门店区域：010
     */
    private String region;
    /**
     * 父级门店：LWBJ
     */
    private String parentCode;

}
