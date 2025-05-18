package com.example.demo.modal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int httpStatus;
    private String code;
    private String message;
    private String clientMessageId;
    private String path;
    private T data;
}
