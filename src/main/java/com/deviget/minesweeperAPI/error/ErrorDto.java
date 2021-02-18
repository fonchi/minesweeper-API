package com.deviget.minesweeperAPI.error;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ErrorDto implements Serializable {

    private int status;
    private String error;
    private String message;
    private Object resource;

    public ErrorDto(int status, String message) {
        super();
        this.error = error;
        this.message = message;
    }

    public ErrorDto(int status, String error, String message) {
        super();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorDto(int status, String error, String message, Object resource) {
        super();
        this.status = status;
        this.error = error;
        this.message = message;
        this.resource = resource;
    }
}
