package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "houseType")
@Deprecated
public class HouseType {

    @Indexed(unique = true)
    private String houseCode;

    private String houseName;

    private Integer houseStatus;

}
