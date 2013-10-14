package io.hebert.autolock;

import java.util.concurrent.locks.Lock;

/**
 * Exception thrown whenever the attempt to acquire a lock in an ARM context failed.
 */
public class TryLockFailedException extends Exception {
    private final Lock lock;

    /**
     * Creates an exception due to a lock which couldn't be acquired.
     *
     * @param lock lock which couldn't be acquired.
     */
    public TryLockFailedException(Lock lock) {
        this.lock = lock;
    }

    public Lock getLock() {
        return lock;
    }
}
