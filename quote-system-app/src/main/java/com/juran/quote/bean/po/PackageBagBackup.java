package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Document(collection = "packageBag_bak")
public class PackageBagBackup {

    @Indexed(unique = true)
    private Long bagId;

    private String bagName;

    @Indexed
    private String bagCode;

    private String bagDesc;

    private Integer bagStatus;

    private BigDecimal amount;

    private Long packageVersionId;

    private Date removeTime;
}
