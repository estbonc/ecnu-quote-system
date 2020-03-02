package com.ecnu.paper.quotesystem.bean.response;


import com.juran.quote.bean.PagingData;
import com.juran.quote.bean.StatusData;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class HttpResponseList<T> {
    private StatusData status;

    private PagingData paging;

    private List<T> data;
}
