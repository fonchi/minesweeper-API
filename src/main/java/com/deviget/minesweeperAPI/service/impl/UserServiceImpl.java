package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.error.NotFoundException;
import com.deviget.minesweeperAPI.repository.UserRepository;
import com.deviget.minesweeperAPI.service.UserService;
import com.deviget.minesweeperAPI.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static java.util.Objects.isNull;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository;

    @Override
    public User createUser(String username, String email, String password) {

        User user = User.builder()
                .id(UniqueIdGenerator.generate())
                .username(username)
                .email(email)
                .password(password)
                .creationDatetime(Instant.now())
                .build();

        return userRepository.save(user);
    }

    /**
     * finds user by id
     *
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {

        User user = userRepository.getByUsername(username);
        if (isNull(user))
            throw new NotFoundException(String.format("User '%s' not found", username));
        logger.info(String.format("user finded:  %s", user.toString()));
        return user;
    }
}
