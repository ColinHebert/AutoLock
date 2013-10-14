# Auto-Lock

Auto-Lock is a library allowing to use a [Lock](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/Lock.html)
in the [ARM (also known as Try-with-resources)](http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
syntax.

Any lock that is used as a resource in a Try-with-resources, will automatically be unlocked once the try block has been
executed.
It my be the case that the lock can't be acquired, in which case a `TryLockFailedException` is thrown and must be
handled.

## Example

This code:
```java
public class Test {
    private Lock lock = new ReentrantLock();

    public void methodA() {
        try {
            if (!lock.tryLock(10, Time.Unit.SECOND))
                throw new TimeoutException();
            try {
                // Do something
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException | TimeoutException e) {
            System.err.println("Couldn't obtain the lock");
        }
    }
}
```

Becomes:
```java
public class Test {
    private AutoLock autoLock = new AutoLockWrapper(new ReentrantLock());

    public void methodA() {
        try(AutoLock ignore = autoLock.autoTryLock(10, Time.Unit.SECOND)) {
            // Do something
        } catch (InterruptedException | TryLockFailedException e) {
            System.err.println("Couldn't obtain the lock");
        }
    }
}
```
