package com.flash3388.flashlib.app.concurrent;

import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class DefaultFlashLibThreadFactory implements NamedThreadFactory {

    private static final Logger LOGGER = Logging.getLogger("Thread");
    private static final String BASE_THREAD_NAME = "FlashLibThread";
    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = new DefaultUncaughtExceptionHandler();

    private int mNextThreadNum;

    public DefaultFlashLibThreadFactory() {
        mNextThreadNum = 0;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = BASE_THREAD_NAME + "." + (++mNextThreadNum);
        return createThread(threadName, r);
    }

    @Override
    public Thread newThread(String name, Runnable r) {
        String threadName = BASE_THREAD_NAME + "." + name;
        return createThread(threadName, r);
    }

    private Thread createThread(String name, Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(EXCEPTION_HANDLER);

        return thread;
    }

    private static class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Uncaught exception from thread {}", t, e);
        }
    }
}
