package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Default root error to be thrown from any Service class.
 */
@Getter
@Setter
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
