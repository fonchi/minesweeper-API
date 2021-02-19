package com.deviget.minesweeperAPI.dto;

import com.deviget.minesweeperAPI.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Builder
@ToString
public class UserResponseDto {

    private String id;
    private String username;
    private String email;
    @JsonProperty(value = "creation_datetime")
    private Instant creationDatetime;

    public static UserResponseDto fromEntity(User user) {

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .creationDatetime(user.getCreationDatetime())
                .build();
    }
}
