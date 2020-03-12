package com.juran.quote.bean.quote;

import lombok.Data;

import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 24/10/2018 1:52 PM
 * @Description: 3d主材类目数据体
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class CategoryDto {

    private String id;
    private List<CategoryDto>  children;
    private String tenantId;
    private String name;
    private String icon;
    private String custAttr;
    private String logo;
    /**
     * 不需要
     */
    private String attributes;
    private String title;
    private String parentId;
    private Integer status;













}
