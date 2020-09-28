package com.jsg.chatterbox.libs.httpexceptions;

import org.springframework.http.HttpStatus;

public final class NotFoundHttpException extends HttpException {

    private static final String DEFAULT_MESSAGE = "The requested resource cannot be found.";

    public NotFoundHttpException() {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public NotFoundHttpException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }

}
