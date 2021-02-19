package com.deviget.minesweeperAPI.lock.impl;

import com.deviget.minesweeperAPI.error.ResourceLockedException;
import com.deviget.minesweeperAPI.lock.Lock;
import com.deviget.minesweeperAPI.lock.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LockServiceImpl implements LockService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO migrate per array implementation to persistence approach to ensure consistency across multiple deployed instances
    private static Set<Lock> locksSet = new HashSet<>();
    private static final long DEFAULT_LOCK_TTL = 60;

    @Override
    public Lock lock(String resourceId) {
        return lock(resourceId, DEFAULT_LOCK_TTL);
    }

    @Override
    public Lock lock(String resourceId, long ttl) {
        if (locksSet.contains(resourceId))
            throw new ResourceLockedException(String.format("Resource already locked: '%s'", resourceId));
        Lock lock = new Lock(resourceId, ttl);
        locksSet.add(lock);
        logger.info("Resource locked: '" + lock.getResourceId() + "'");
        asyncUnlock(lock);
        return lock;
    }

    @Override
    public void unlock(Lock lock) {
        locksSet.remove(lock);
        logger.info("Resource unlocked: '" + lock.getResourceId() + "'");
    }

    private void asyncUnlock(Lock lock) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(lock.getTtl());
            } catch (InterruptedException e) {
                logger.error("InterruptedException unlocking resource: '" + lock.getResourceId() + "'");
            } finally {
                locksSet.remove(lock);
            }
        }).start();
    }
}
