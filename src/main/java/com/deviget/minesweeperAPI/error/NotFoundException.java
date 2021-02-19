package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    public static final String ERROR = "Not found error";
    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

    public String getError() {
        return ERROR + ": " + getMessage();
    }
}
