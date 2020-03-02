package com.ecnu.paper.quotesystem.bean.po;

import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 5:04 PM
 * @Description: 户型po
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Document(collection = "houseType")
@CompoundIndexes({
        @CompoundIndex(name = "type_company_status",def = "{'houseType':1,'decorationCompany':1,'status':1}",unique = true)
})
public class HouseTypePo extends BasePo {
    /**
     * 户型id
     */
    private Long houseTypeId;
    /**
     *户型名称
     */
    private String houseType;

    /**
     *户型编码
     */
    private String houseTypeCode;
    /**
     *装饰公司
     */
    private String decorationCompany;
    /**
     *空间列表
     */
    private Set<Long> rooms = Sets.newHashSet();
    /**
     *备注
     */
    private String remark;
    /**
     *状态
     */
    private Integer status;

}
