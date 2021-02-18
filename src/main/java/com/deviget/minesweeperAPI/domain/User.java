package com.deviget.minesweeperAPI.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {

    private String id;
    private String username;
    private String email;
    private String password;
    private String token;
}
