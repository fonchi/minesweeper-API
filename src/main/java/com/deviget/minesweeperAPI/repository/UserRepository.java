package com.deviget.minesweeperAPI.repository;

import com.deviget.minesweeperAPI.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User getByUsername(String username);
}
