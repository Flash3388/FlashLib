package com.flash3388.flashlib.util.logging.jul;

import java.util.concurrent.ThreadFactory;

public class FlusherThreadFactory implements ThreadFactory {

    private static final int LOG_FLUSHER_STACK_SIZE_KB = 128;
    private static final ThreadGroup FLUSHING_THREAD_GROUP = new ThreadGroup("flusher");

    private final String mName;

    public FlusherThreadFactory(String name) {
        mName = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(FLUSHING_THREAD_GROUP, r,
                mName.concat("-log-flusher"), LOG_FLUSHER_STACK_SIZE_KB);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);

        return thread;
    }
}
