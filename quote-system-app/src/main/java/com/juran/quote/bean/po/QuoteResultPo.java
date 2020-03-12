package com.juran.quote.bean.po;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.juran.quote.bean.dto.QuoteRoomDetailDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/7/13 16:07
 * @description
 */
@Document(collection = "quoteResult")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResultPo {

    /**
     * 报价唯一标识
     */
    private Long quoteId;

    /**
     * 3D-设计方案ID
     */
    private String designId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 空间主材信息
     */
    private List<QuoteRoomDetailDto> roomList;

}
