package com.ecnu.paper.quotesystem.bean.dto;

import lombok.Data;

@Data
public class ServiceResponse<T> {
    private Boolean status;
    private String message;

    private T data;
    public ServiceResponse(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServiceResponse(Boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "status=" + status +
                ", message='" + message +
                '}';
    }
}
