package edu.flash3388.flashlib.cam.jpeg.client;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;
import edu.flash3388.flashlib.cam.jpeg.reader.JpegReader;
import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.util.concurrent.ExecutorCloser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MjpegClient {

    private final ExecutorService mExecutorService;
    private final Logger mLogger;

    private final AtomicReference<JpegReader> mReaderReference;
    private final AtomicBoolean mIsTerminated;

    public MjpegClient(ExecutorService executorService, Logger logger) {
        mExecutorService = executorService;
        mLogger = logger;

        mReaderReference = new AtomicReference<>();
        mIsTerminated = new AtomicBoolean(false);
    }

    public boolean isTerminated() {
        return mIsTerminated.get();
    }

    public boolean isRunning() {
        return mReaderReference.get() != null;
    }


    public void start(JpegReader jpegReader, Consumer<JpegImage> imageConsumer) {
        if (isTerminated()) {
            throw new IllegalStateException("terminated");
        }
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        mReaderReference.set(jpegReader);
        mExecutorService.submit(new MjpegReadTask(jpegReader, imageConsumer, mLogger));
    }

    public void terminate() {
        if (isTerminated()) {
            throw new IllegalStateException("already terminated");
        }
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        try {
            Closer closer = Closer.empty();

            JpegReader reader = mReaderReference.getAndSet(null);
            closer.add(reader);

            closer.add(new ExecutorCloser(mExecutorService));

            closer.close();
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "error while closing resources", e);
        }
    }
}
