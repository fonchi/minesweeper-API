package com.deviget.minesweeperAPI.repository;

import com.deviget.minesweeperAPI.domain.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {

    Board findByIdAndUserId(String id, String userId);
}
