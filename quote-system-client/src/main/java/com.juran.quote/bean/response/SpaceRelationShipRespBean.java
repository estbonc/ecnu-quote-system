package com.juran.quote.bean.response;

import lombok.Data;

/**
 * @Author: xiongtao
 * @Date: 23/10/2018 4:45 PM
 * @Description: 空间查询返回vo
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class SpaceRelationShipRespBean {

    /**
     * 空间id
     */
    private Long roomId;

    /**
     * 空间名称
     */
    private String roomName;

    /**
     * 空间id
     */
    private Integer hasBind;

    private String roomType;



}
