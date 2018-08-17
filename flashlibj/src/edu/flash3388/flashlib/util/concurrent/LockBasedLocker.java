package edu.flash3388.flashlib.util.concurrent;

import edu.flash3388.flashlib.util.Operation;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class LockBasedLocker implements Locker {

    private final Lock mLock;

    private LockBasedLocker(Lock lock) {
        mLock = Objects.requireNonNull(lock);
    }

    public static LockBasedLocker with(Lock lock) {
        return new LockBasedLocker(lock);
    }

    @Override
    public <R> R run(Operation<R> operation) {
        mLock.lock();
        try {
            return operation.run();
        } finally {
            mLock.unlock();
        }
    }
}
