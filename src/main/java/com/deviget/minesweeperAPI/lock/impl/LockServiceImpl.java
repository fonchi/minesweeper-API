package com.deviget.minesweeperAPI.lock.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.lock.BoardLock;
import com.deviget.minesweeperAPI.lock.Lock;
import com.deviget.minesweeperAPI.lock.LockService;
import org.springframework.stereotype.Service;

@Service
public class LockServiceImpl implements LockService {

    @Override
    public BoardLock lock(Board board) {
        return new BoardLock(board);
    }

    @Override
    public Lock lock(String resourceId) {
        //TODO
        return null;
    }

    @Override
    public void unlock(Lock lock) {
        //TODO
    }
}
