package com.jsg.chatterbox.libs.httpexceptions;

import org.springframework.http.HttpStatus;

public abstract class HttpException extends Exception {

    private final HttpStatus statusCode;
    private final String message;

    public HttpException(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return message;
    }

}
