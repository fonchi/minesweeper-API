package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4486285316041419025L;

    private HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }
}
