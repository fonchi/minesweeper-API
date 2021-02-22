package com.deviget.minesweeperAPI.error;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    public static final String ERROR = "Bad request error";
    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }

    public String getError() {
        return ERROR + ": " + getMessage();
    }
}
