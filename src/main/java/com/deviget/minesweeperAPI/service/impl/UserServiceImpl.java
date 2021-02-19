package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.service.UserService;
import com.deviget.minesweeperAPI.util.UniqueIdGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(String username, String email, String password) {
        //TODO implement persistency
        return User.builder()
                .id(UniqueIdGenerator.generate())
                .username(username)
                .email(email)
                .password(password)
                .creationDatetime(Instant.now())
                .build();
    }

    /**
     * finds user by id
     *
     * @param id
     * @return
     */
    @Override
    public User getUser(String id) {
        //TODO implement persistency
        return User.builder().id(id).build();
    }
}
