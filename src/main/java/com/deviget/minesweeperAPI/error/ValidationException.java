package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.Errors;

@Getter
@Setter
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = -4423285343531419054L;

    private Errors errors;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(Errors errors) {
        super();
        this.errors = errors;
    }
}
