package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(User user) {
        //TODO implement persistency
        return null;
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
