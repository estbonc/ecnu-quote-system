package com.ecnu.paper.quotesystem.bean.response;

import java.io.Serializable;


public class CommonRespBean<T> implements Serializable {

    private static final long serialVersionUID = -7361374995796005268L;

    private T data;

    private Status status;

    private PageRespBean<T> page;

    public PageRespBean<T> getPage() {
        return page;
    }

    public void setPage(PageRespBean<T> page) {
        this.page = page;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public CommonRespBean(T data) {
        this.data = data;
    }

    public CommonRespBean() {
    }

    public CommonRespBean(Status status) {
        this.status = status;
    }

    public CommonRespBean(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public CommonRespBean(T data, Status status, PageRespBean<T> page) {
        this.data = data;
        this.status = status;
        this.page = page;
        page.setData(null);
    }
    public static class Status {
        public static final Integer SUCCESS = 0;
        public static final Integer DATA_EXISTING = 1;
        public static final Integer EXCEPTION = 2;
        public static final Integer DATA_NOT_EXISTING = 3;
        public static final Integer DELETING_BACKUP_ERROR = 4;
        public static final Integer DELETING_FAIL = 5;
        public static final Integer CREATION_FAIL = 6;
        public static final Integer UPDATE_FAIL = 7;
        public static final Integer DATA_IN_USE = 8;
        public static final Integer INPUT_ERROR = 9;
        private Integer code;
        private String message;

        public Status(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
