package com.example.screenstreamer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class StatusException extends HttpStatusCodeException {
    public StatusException(HttpStatus statusCode) {
        super(statusCode);
    }
}
