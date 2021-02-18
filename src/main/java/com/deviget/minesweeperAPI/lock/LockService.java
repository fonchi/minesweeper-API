package com.deviget.minesweeperAPI.lock;

import com.deviget.minesweeperAPI.domain.Board;

public interface LockService {

    BoardLock lock(Board board);

    Lock lock(String resourceId);

    void unlock(Lock lock);

}
