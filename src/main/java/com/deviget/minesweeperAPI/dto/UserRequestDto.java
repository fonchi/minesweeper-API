package com.deviget.minesweeperAPI.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@ToString
public class UserRequestDto {

    private String username;
    @Email
    private String email;
    private String password;

}
