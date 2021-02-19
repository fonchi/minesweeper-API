package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Default root error to be thrown from any Service class.
 */
@Getter
public class InternalServerException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    public static final String ERROR = "Internal server error";
    public static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerException(String message) {
        super(message);
    }

    public String getError() {
        return ERROR + ": " + getMessage();
    }
}
