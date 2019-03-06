package com.flash3388.flashlib.util.logging.jul;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DelegatingHandler extends Handler {

    private final Queue<LogRecord> mLogRecords;
    private final Handler mDelegatedHandler;

    public DelegatingHandler(Handler delegatedHandler, Queue<LogRecord> logRecordsQueue) {
        mDelegatedHandler = delegatedHandler;
        mLogRecords = logRecordsQueue;
    }

    public DelegatingHandler(Handler delegatedHandler, int recordsCapacity) {
        this(delegatedHandler, new ArrayBlockingQueue<>(recordsCapacity));
    }

    @Override
    public void publish(LogRecord record) {
        mLogRecords.offer(record);
    }

    @Override
    public void flush() {
        while (!mLogRecords.isEmpty()) {
            LogRecord record = mLogRecords.poll();
            if (record == null) {
                break;
            }

            mDelegatedHandler.publish(record);
        }

        mDelegatedHandler.flush();
    }

    @Override
    public void close() throws SecurityException {
        flush();
        mDelegatedHandler.close();
    }
}
