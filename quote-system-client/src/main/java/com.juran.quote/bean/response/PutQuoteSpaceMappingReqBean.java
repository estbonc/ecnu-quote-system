package com.juran.quote.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新报价-空间映射
 */
@Data
public class PutQuoteSpaceMappingReqBean implements Serializable {
    private static final long serialVersionUID = 4494552408325939618L;

    /**
     * 报价ID
     */
    private Long quoteId;
    /**
     * 空间匹配信息
     */
    private List<GetQuoteSpaceMappingRespBean> mappingList;
}
