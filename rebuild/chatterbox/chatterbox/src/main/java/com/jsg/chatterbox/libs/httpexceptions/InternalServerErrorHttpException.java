package com.jsg.chatterbox.libs.httpexceptions;

import org.springframework.http.HttpStatus;

public final class InternalServerErrorHttpException extends HttpException {

    private static final String DEFAULT_MESSAGE = "Something went wrong when performing this request.";

    public InternalServerErrorHttpException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE);
    }

    public InternalServerErrorHttpException(String msg) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

}
