package com.deviget.minesweeperAPI.error;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }
}
