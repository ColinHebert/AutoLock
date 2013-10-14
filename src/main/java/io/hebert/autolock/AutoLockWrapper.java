package io.hebert.autolock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Wrapper for {@link Lock} instances to use them in the ARM context.
 */
public class AutoLockWrapper implements AutoLock {
    private final Lock lock;

    /**
     * Wraps the given Lock in an AutoLock.
     *
     * @param lock lock to wrap.
     */
    public AutoLockWrapper(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock();
    }

    @Override
    public AutoLock autoLock() {
        lock.lock();
        return this;
    }

    @Override
    public AutoLock autoLockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
        return this;
    }

    @Override
    public AutoLock autoTryLock() throws TryLockFailedException {
        if (!lock.tryLock())
            throw new TryLockFailedException(this);
        return this;
    }

    @Override
    public AutoLock autoTryLock(long time, TimeUnit unit) throws InterruptedException, TryLockFailedException {
        if (!lock.tryLock(time, unit))
            throw new TryLockFailedException(this);
        return this;
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public void close() {
        unlock();
    }
}
