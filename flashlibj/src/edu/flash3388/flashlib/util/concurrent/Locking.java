package edu.flash3388.flashlib.util.concurrent;

import java.util.concurrent.locks.Lock;

public class Locking {

    private Locking() {}

    public static Locker synchronizedBlockLocker(Object lockObject) {
        return SynchronizedBlockLocker.with(lockObject);
    }

    public static Locker lockBasedLocker(Lock lock) {
        return LockBasedLocker.with(lock);
    }
}
