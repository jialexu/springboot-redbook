package com.chuwa.redbook.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for API business logic errors.
 * Allows custom HTTP status and message.
 */
public class BlogAPIException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BlogAPIException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
