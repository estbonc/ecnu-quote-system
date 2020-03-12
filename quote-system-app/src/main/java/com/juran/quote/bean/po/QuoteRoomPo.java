package com.juran.quote.bean.po;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Collections;
import java.util.List;
@Data
@Document(collection = "quoteRoom")
public class QuoteRoomPo {
    /**
     * 空间报价ID
     */
    private Long quoteRoomId;
    /**
     * 报价ID
     */
    private Long quoteId;
    /**
     * 房型
     */
    private String houseType;
    /**
     * 空间信息
     */
    private String roomType;
    /**
     * 空间ID
     */
    private String roomId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 空间内的施工项
     */
    private List<QuoteRoomConstructPo> constructList = Collections.EMPTY_LIST;
}