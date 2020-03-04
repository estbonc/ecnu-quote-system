package com.ecnu.paper.quotesystem.bean.po;

import com.ecnu.paper.quotesystem.bean.dto.BindMaterial;
import com.ecnu.paper.quotesystem.bean.dto.BindRoom;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document(collection = "constructRelationship")
public class ConstructRelationship extends BasePo{
    /**
     * 施工项关系id
     */
    private Long constructRelationshipId;

    /**
     * 施工项id
     */
    private Long constructId;

    /**
     * 施工项编码
     */
    private String constructCode;

    /**
     * 装饰公司
     */
    private String  decorationCompany;

    /**
     * 装修类型
     */
    private Long quoteTypeId;

    /**
     * 户型
     */
    private Long houseTypeId;

    /**
     * 绑定的材料集合
     */
    private List<BindMaterial> bindMaterials= Lists.newArrayList();

    /**
     * 绑定的房间集合
     */
    private List<BindRoom> bindRooms= Lists.newArrayList();

}
