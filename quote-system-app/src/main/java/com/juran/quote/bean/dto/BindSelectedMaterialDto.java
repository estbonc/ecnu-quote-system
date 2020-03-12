package com.juran.quote.bean.dto;

import com.juran.quote.bean.po.SelectedMaterial;
import lombok.Data;

import java.util.List;

@Data
public class BindSelectedMaterialDto {
    private String materialCode;
    private List<SelectedMaterial> materialList;
    private Integer bindByDefault;
}
