package com.juran.quote.bean.request;

import com.google.common.collect.Lists;
import com.juran.quote.bean.quote.SelectedConstructDTO;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.util.List;

@Data
public class PackageRoomRequestBean implements Serializable {

    private static final long serialVersionUID = -6608061901674599226L;

    /**
     * 关联套餐版本ID
     */
    @QueryParam("packageVersionId")
    @ApiParam(value = "套餐版本ID", required = true, example = "300990")
    private Long packageVersionId;

    /**
     * 房屋户型
     */
    @QueryParam("houseType")
    @ApiParam(value = "房屋户型", required = true, example = "L2B2")
    private String houseType;

    /**
     * 房间类型
     */
    @QueryParam("roomType")
    @ApiParam(value = "房间类型", required = true, example = "BATH_ROOM")
    private String roomType;

    /**
     * 排序序号
     */
    @QueryParam("sort")
    @ApiParam(value = "排序", required = true, example = "5")
    private Integer sort;

    /**
     * 选择的施工项
     */
    @QueryParam("selectedConstruct")
    @ApiParam(value = "选择的施工项")
    private List<SelectedConstructDTO> selectedConstruct = Lists.newArrayList();

    /**
     * 选择的主材
     */
    @QueryParam("selectedMaterial")
    @ApiParam(value = "选择的主材", required = true)
    private List<Long> selectedMaterial;

    @Override
    public String toString() {
        return "PackageRoomRequestBean{" +
                ", packageVersionId=" + packageVersionId +
                ", houseType='" + houseType + '\'' +
                ", roomType='" + roomType + '\'' +
                ", sort=" + sort +
                ", selectedConstruct=" + selectedConstruct +
                ", selectedMaterial=" + selectedMaterial +
                '}';
    }
}
