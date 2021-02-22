package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.User;

public interface UserService {

    User createUser(String username, String email, String password);

    User getUserByUsername(String username);
}
