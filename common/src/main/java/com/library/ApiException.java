package com.library;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{
    private final String errorMessage;
    private final ErrorInterface errorType;
    private final HttpStatus httpStatus;

    public ApiException(String errorMessage, ErrorInterface errorType, HttpStatus httpStatus) {
        this.errorMessage = errorMessage;
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }
}
