package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "packageRoom_bak")
public class PackageRoomBackup {

    /**
     * 套餐空间id
     */
    @Indexed(unique = true)
    private Long packageRoomId;
    /**
     * 关联套餐版本ID
     */
    private Long packageVersionId;

    /**
     * 房型
     */
    private String houseType;

    /**
     * 空间信息
     */
    private String roomType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 选择的施工项
     */
    private List<SelectedConstruct> selectedConstruct;

    /**
     * 选择的主材
     */
    private List<Long> selectedMaterial;

    /**
     * 删除时间
     */
    private Date removeDate;
}
