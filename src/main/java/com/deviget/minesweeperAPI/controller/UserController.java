package com.deviget.minesweeperAPI.controller;

import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.dto.UserRequestDto;
import com.deviget.minesweeperAPI.dto.UserResponseDto;
import com.deviget.minesweeperAPI.error.InternalServerException;
import com.deviget.minesweeperAPI.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static java.util.Objects.isNull;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRequestDto dto) {

        logger.info("UserRequestDto: " + dto.toString());

        User user = userService.createUser(dto.getUsername(), dto.getEmail(), dto.getPassword());
        if (isNull(user))
            throw new InternalServerException("Error to create user");
        logger.info("User created: " + user.toString());

        UserResponseDto responseDto = UserResponseDto.fromEntity(user);
        logger.info("UserResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    //TODO GET User By Username
}
