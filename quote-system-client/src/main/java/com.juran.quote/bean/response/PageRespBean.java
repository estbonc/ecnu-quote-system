package com.juran.quote.bean.response;

import java.util.List;

public class PageRespBean<T> {
    private long total;
    private Integer offset;
    private Integer limit;
    private List<T> data;

    public PageRespBean() {
    }

    public List<T> getData() {
        return data;
    }

    public PageRespBean(long total, Integer offset, Integer limit) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
