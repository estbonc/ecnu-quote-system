package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 2:44 PM
 * @Description: 空间
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Document(collection = "room")
@CompoundIndexes({
        @CompoundIndex(name = "name_status",def = "{'roomName':1,'status':1}",unique = true)
})
public class RoomPo extends BasePo{
    /**
     * 空间id
     */
    private Long roomId;
    /**
     * 空间名称
     */
    private String roomName;

    /**
     * 对应3D roomType
     */
    private String englishName;
    /**
     * 状态
     */
    private Integer status;
}
