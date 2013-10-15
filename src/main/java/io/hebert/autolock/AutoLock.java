package io.hebert.autolock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Auto-Closeable lock which allows to use a {@link Lock} with the ARM syntax.
 * <p>
 * Usage:
 * <code>
 * try(AutoLock ignore = autoLock.autoLock()){
 * // Action
 * }
 * </code>
 * </p>
 */
public interface AutoLock extends Lock, AutoCloseable {
    /**
     * Acquires the Lock.
     * <p>
     * Acquire the lock, if the lock isn't available keep the current Thread dormant until the lock is
     * acquired.
     * </p>
     *
     * @return the current AutoLock for compatibility reason with ARM.
     * @see #lock()
     */
    AutoLock autoLock();

    /**
     * Acquires the Lock unless the current thread is interrupted.
     *
     * @return the current AutoLock for compatibility reason with ARM.
     * @throws InterruptedException the current thread has been interrupted while waiting for the lock.
     * @see #lockInterruptibly()
     */
    AutoLock autoLockInterruptibly() throws InterruptedException;

    /**
     * Attempts to acquire the Lock.
     * <p>
     * If the lock can't be acquired throw a {@link io.hebert.autolock.TryLockFailedException}.
     * </p>
     *
     * @return the current AutoLock for compatibility reason with ARM.
     * @throws TryLockFailedException when the lock couldn't be acquired.
     * @see #tryLock()
     */
    AutoLock autoTryLock() throws TryLockFailedException;

    /**
     * Attempts to acquire the Lock in a given amount of time.
     * <p>
     * If the lock can't be acquired throw a {@link io.hebert.autolock.TryLockFailedException}.
     * </p>
     *
     * @param time the maximum amount time to wait for the lock.
     * @param unit the unit of time used by the {@code time} argument.
     * @return the current AutoLock for compatibility reason with ARM.
     * @throws InterruptedException   the current thread has been interrupted while waiting for the lock.
     * @throws TryLockFailedException when the lock couldn't be acquired in the given time.
     * @see #tryLock(long, TimeUnit)
     */
    AutoLock autoTryLock(long time, TimeUnit unit) throws InterruptedException, TryLockFailedException;

    /**
     * Disengage the lock.
     * <p>
     * Do not throw a checked exception but may throw a {@link RuntimeException}.
     * </p>
     */
    @Override
    void close();
}
