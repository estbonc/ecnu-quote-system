package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "roomType")
public class RoomType {

    @Indexed(unique = true)
    private String typeCode;

    private String typeName;

    private Integer typeStatus;

}
