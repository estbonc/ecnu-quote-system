package com.juran.quote.bean.dto;

import com.juran.quote.bean.response.QuoteBaseInfoRespBean;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class GetQuoteDetailDto extends QuoteBaseInfoRespBean {
    private static final long serialVersionUID = -3842767706527902319L;

    private List<QuoteRoomDto> rooms;

    private List<ChargeTypeDto> extraCharges = Collections.EMPTY_LIST;
}
