package edu.flash3388.flashlib.util;

import java.util.concurrent.locks.Lock;

public class Locker {

    private final Lock mLock;

    private Locker(Lock lock) {
        mLock = lock;
    }

    public static Locker with(Lock lock) {
        return new Locker(lock);
    }

    public void run(Runnable runnable) {
        mLock.lock();
        try {
            runnable.run();
        } finally {
            mLock.unlock();
        }
    }
}
