package com.ecnu.paper.quotesystem.utils;

import java.util.List;

public class PageModel<T> {

    /**
     * 结果集
     */
    private List<T> data;

    /**
     * 查询记录数
     */
    private long count;

    /**
     * 每页多少条数据
     */
    private int limit;

    /**
     * 偏移量
     */
    private int offset;

    /**
     * 总页数
     *
     * @return
     */
    public int getTotalPages() {
        return ((int) count + limit - 1) / limit;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
