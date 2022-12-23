package com.flash3388.flashlib.robot.hid;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataStore {

    private final Lock mLock;
    private final Queue<RawHidData> mDataQueue;

    public DataStore() {
        mLock = new ReentrantLock();
        mDataQueue = new ArrayDeque<>(3);
        mDataQueue.add(new RawHidData());
        mDataQueue.add(new RawHidData());
        mDataQueue.add(new RawHidData());
    }

    public void add(RawHidData data) {
        if (data == null) {
            return;
        }

        mLock.lock();
        try {
            mDataQueue.add(data);
        } finally {
            mLock.unlock();
        }
    }

    public RawHidData retrieve() {
        mLock.lock();
        try {
            RawHidData data = mDataQueue.poll();
            if (data == null) {
                data = new RawHidData();
            }

            return data;
        } finally {
            mLock.unlock();
        }
    }
}
