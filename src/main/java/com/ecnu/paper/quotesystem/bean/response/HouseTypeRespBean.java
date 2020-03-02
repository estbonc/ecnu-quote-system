package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class HouseTypeRespBean implements Serializable {
    private static final long serialVersionUID = -4051793520081216680L;
    private String type;
    private String name;
}
