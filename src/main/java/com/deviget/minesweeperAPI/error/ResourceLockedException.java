package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceLockedException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    public static final String ERROR = "Resource locked error";
    public static final HttpStatus STATUS = HttpStatus.LOCKED;

    public ResourceLockedException(String message) {
        super(message);
    }

    public String getError() {
        return ERROR + ": " + getMessage();
    }
}
