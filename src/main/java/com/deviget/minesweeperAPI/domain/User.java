package com.deviget.minesweeperAPI.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class User {

    private String id;
    private String username;
    private String email;
    private String password;
    private String token;
}
