package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.User;

public interface UserService {

    User createUser(User user);

    User getUser(String id);
}
