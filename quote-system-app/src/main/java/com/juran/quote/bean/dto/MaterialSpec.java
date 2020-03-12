package com.juran.quote.bean.dto;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 29/08/2018 1:46 PM
 * @Description: 规格绑定关系
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class MaterialSpec {
    /**
     * 主材、辅材的规格
     */
    private String  spec;
    /**
     * 是否默认带出
     */
    private  Integer bindByDefault;








}
