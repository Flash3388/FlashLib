package com.flash3388.flashlib.app.concurrent;

import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class DefaultFlashLibThreadFactory {

    private static final Logger LOGGER = Logging.getLogger("Thread");
    private static final String BASE_THREAD_NAME = "FlashLibThread";
    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = new DefaultUncaughtExceptionHandler();


    public static Thread newThread(String name, Runnable r) {
        String threadName = BASE_THREAD_NAME + "." + name;

        Thread thread = new Thread(r);
        thread.setName(threadName);
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
