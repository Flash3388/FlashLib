package edu.flash3388.flashlib.util.concurrent;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTerminator implements Closeable {

    private final ExecutorService mExecutorService;

    public ExecutorTerminator(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public void close() throws IOException {
        mExecutorService.shutdownNow();
        try {
            while (!mExecutorService.awaitTermination(1, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
