package com.deviget.minesweeperAPI.lock;

import com.deviget.minesweeperAPI.domain.Board;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BoardLock implements AutoCloseable {

    @Autowired
    private LockService lockService;
    private List<Lock> locks = new ArrayList<>();

    public BoardLock lock(Board board) {
        locks.add(lockService.lock(board.getId()));
        return this;
    }

    @Override
    public void close() {
        for (Lock l : locks)
            lockService.unlock(l);
    }
}
