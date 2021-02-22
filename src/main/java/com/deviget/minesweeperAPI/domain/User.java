package com.deviget.minesweeperAPI.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

import static java.util.Objects.nonNull;

@Getter
@Setter
@Builder
@ToString
public class User {

    private String id;
    private String username;
    private String email;
    private String password;
    private Instant creationDatetime;

    public boolean areIdempotent(String username, String email) {
        return (nonNull(username) && username.equals(this.username))
                && (nonNull(email) && email.equals(this.email));
    }
}
