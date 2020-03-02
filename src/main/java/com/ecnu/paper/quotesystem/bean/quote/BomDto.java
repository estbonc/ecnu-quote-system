package com.ecnu.paper.quotesystem.bean.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 3D工具-bom清单汇总
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BomDto implements Serializable{
    private static final long serialVersionUID = -7725686297799733062L;
    /**
     * 3d设计方案-唯一标识-uuid
     */
    private String designID;
    /**
     * 设计家3d案例唯一标识
     */
    private String acsAssetID;
    /**
     * 设计家-项目唯一标识
     */
    private Long acsProjectID;
    /**
     * 房型信息
     */
    private String roomNum;
    /**
     * 装饰公司信息
     */
    private String decorationCompany;
    /**
     * 设计方案名称
     */
    private String designName;
    /**
     * 方案描述
     */
    private String description;
    /**
     * 设计师编号
     */
    private String userId;
    /**
     * 套餐类型
     */
    private String couponTypes;
    /**
     * 套内面积
     */
    private BigDecimal couponArea;
    /**
     * 空间列表
     */
    private List<BomRoomDto> roomList;
    /**
     * 主材列表
     */
    private List<BomItemDto> bomList;

    public BomDto() {
    }
}
