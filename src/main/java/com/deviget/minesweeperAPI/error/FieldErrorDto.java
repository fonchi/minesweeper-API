package com.deviget.minesweeperAPI.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldErrorDto {

    private String field;
    private String code;
    private String message;
}
