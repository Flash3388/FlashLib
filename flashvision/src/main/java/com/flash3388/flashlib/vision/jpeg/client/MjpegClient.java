package com.flash3388.flashlib.vision.jpeg.client;

import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.http.HttpConnectionCloser;
import com.flash3388.flashlib.vision.ImagePipeline;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.jpeg.reader.MjpegReader;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MjpegClient implements Closeable {

    private final URL mStreamUrl;
    private final ExecutorService mExecutorService;
    private final Logger mLogger;

    private final AtomicReference<HttpURLConnection> mConnectionReference;
    private final AtomicBoolean mIsTerminated;

    public MjpegClient(URL streamUrl, ExecutorService executorService, Logger logger) {
        mStreamUrl = streamUrl;
        mExecutorService = executorService;
        mLogger = logger;

        mConnectionReference = new AtomicReference<>();
        mIsTerminated = new AtomicBoolean(false);
    }

    public static MjpegClient create(URL url, Logger logger) {
        return new MjpegClient(url, Executors.newSingleThreadExecutor(), logger);
    }

    public boolean isTerminated() {
        return mIsTerminated.get();
    }

    public boolean isRunning() {
        return mConnectionReference.get() != null;
    }


    public void start(ImagePipeline<JpegImage> imageConsumer) throws IOException {
        if (isTerminated()) {
            throw new IllegalStateException("terminated");
        }
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        HttpURLConnection connection = (HttpURLConnection) mStreamUrl.openConnection();
        mConnectionReference.set(connection);

        mExecutorService.submit(new MjpegReadTask(new MjpegReader(connection.getInputStream()), imageConsumer, mLogger));
    }

    @Override
    public void close() {
        if (isTerminated()) {
            throw new IllegalStateException("already terminated");
        }
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        try {
            Closer closer = Closer.empty();

            HttpURLConnection connection = mConnectionReference.getAndSet(null);
            closer.add(new HttpConnectionCloser(connection));

            closer.add(new ExecutorCloser(mExecutorService));

            closer.close();
        } catch (IOException e) {
            mLogger.error("Error while closing client", e);
        }
    }
}
