package edu.flash3388.flashlib.util.concurrent;

import edu.flash3388.flashlib.util.Operation;

import java.util.Objects;

public class SynchronizedBlockLocker implements Locker {

    private final Object mLockObject;

    private SynchronizedBlockLocker(Object lockObject) {
        mLockObject = Objects.requireNonNull(lockObject);
    }

    public static SynchronizedBlockLocker with(Object lockObject) {
        return new SynchronizedBlockLocker(lockObject);
    }

    @Override
    public <R> R run(Operation<R> operation) {
        synchronized (mLockObject) {
            return operation.run();
        }
    }
}
