package com.ecnu.paper.quotesystem.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author: xiongtao
 * @Date: 10/09/2018 5:39 PM
 * @Description: 数据字典
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Document(collection = "dictionary")
@CompoundIndexes({
        @CompoundIndex(name = "type_code",def = "{'type':1,'code':1}",unique = true)
})
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1780487688220152398L;
    /**
     * 字段分类
     */
    private  String type;

    /**
     * 字段编码
     */
    private  String code;

    /**
     * 字段名称
     */
    private  String name;

    /**
     * 字段英文name
     */
    private  String englishName;

    /**
     * 字段描述
     */
    private  String  description;

    /**
     * 优先级
     */
    private  Integer  priority;

    /**
     * 状态
     */
    private  Integer  status;




}
