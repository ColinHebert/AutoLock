package io.hebert.autolock;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

/**
 * Tests for the {@link AutoLockWrapper}.
 * <p>
 * Some of those tests aren't useful as they're testing the syntax of ARM rather than the {@code AutoLockWrapper} itself.
 * </p>
 */
public class AutoLockWrapperTest {
    @Mock
    private Lock lock;
    private AutoLock autoLock;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        autoLock = new AutoLockWrapper(lock);
    }

    @Test
    public void testLockDoClose() throws Exception {
        try (AutoLock ignore = autoLock.autoLock()) {
            assert true; // NOOP
        }

        verify(lock).unlock();
    }

    @Test
    public void testLockInterruptiblyDoClose() throws Exception {
        try (AutoLock ignore = autoLock.autoLockInterruptibly()) {
            assert true; // NOOP
        }

        verify(lock).unlock();
    }

    @Test
    public void testSuccessfulTryLockDoClose() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        try (AutoLock ignore = autoLock.autoTryLock()) {
            assert true; // NOOP
        }

        verify(lock).unlock();
    }

    @Test
    public void testSuccessfulTryLocWithTimeoutkDoClose() throws Exception {
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);

        try (AutoLock ignore = autoLock.autoTryLock(10, TimeUnit.MILLISECONDS)) {
            assert true; // NOOP
        }

        verify(lock).unlock();
    }

    @Test
    public void testInterruptedLockInterruptiblyDoNotClose() throws Exception {
        doThrow(new InterruptedException()).when(lock).lockInterruptibly();

        try (AutoLock ignore = autoLock.autoLockInterruptibly()) {
            fail("The try block shouldn't be executed");
        } catch (InterruptedException e) {
            assert true; // NOOP
        }

        verify(lock, never()).unlock();
    }

    @Test
    public void testFailedTryLockDoNotClose() throws Exception {
        when(lock.tryLock()).thenReturn(false);

        try (AutoLock ignore = autoLock.autoTryLock()) {
            fail("The try block shouldn't be executed");
        } catch (TryLockFailedException e) {
            assert true; // NOOP
        }

        verify(lock, never()).unlock();
    }

    @Test
    public void testFailedTryLockWitTimeoutDoNotClose() throws Exception {
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);

        try (AutoLock ignore = autoLock.autoTryLock(10, TimeUnit.MILLISECONDS)) {
            fail("The try block shouldn't be executed");
        } catch (TryLockFailedException e) {
            assert true; // NOOP
        }

        verify(lock, never()).unlock();
    }

    @Test
    public void testDoubleLockWithSuccessDoClose() throws Exception {
        Lock secondLock = mock(Lock.class);
        when(lock.tryLock()).thenReturn(true);
        when(secondLock.tryLock()).thenReturn(true);

        try (AutoLock ignore = autoLock.autoTryLock();
             AutoLock ignore2 = new AutoLockWrapper(secondLock).autoTryLock()) {
            assert true; // NOOP
        }

        verify(lock).unlock();
        verify(secondLock).unlock();
    }

    @Test
    public void testDoubleLockWithSecondFailingDoCloseFirstOne() throws Exception {
        Lock secondLock = mock(Lock.class);
        when(lock.tryLock()).thenReturn(true);
        when(secondLock.tryLock()).thenReturn(false);

        try (AutoLock ignore = autoLock.autoTryLock();
             AutoLock ignore2 = new AutoLockWrapper(secondLock).autoTryLock()) {
            fail("The try block shouldn't be executed");
        } catch (TryLockFailedException e) {
            assert true; // NOOP
        }

        verify(lock).unlock();
        verify(secondLock, never()).unlock();
    }

    @Test
    public void testDoubleLockWithFirstFailingDoCloseSecondOne() throws Exception {
        Lock secondLock = mock(Lock.class);
        when(lock.tryLock()).thenReturn(false);
        when(secondLock.tryLock()).thenReturn(true);

        try (AutoLock ignore = autoLock.autoTryLock();
             AutoLock ignore2 = new AutoLockWrapper(secondLock).autoTryLock()) {
            fail("The try block shouldn't be executed");
        } catch (TryLockFailedException e) {
            assert true; // NOOP
        }

        verify(lock, never()).unlock();
        verify(secondLock, never()).unlock();
    }
}
