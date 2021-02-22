package com.deviget.minesweeperAPI.lock;

public interface LockService {

    Lock lock(String resourceId);

    Lock lock(String resourceId, long ttl);

    void unlock(Lock lock);

}
