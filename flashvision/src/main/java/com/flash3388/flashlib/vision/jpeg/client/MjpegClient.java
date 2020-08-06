package com.flash3388.flashlib.vision.jpeg.client;

import com.castle.concurrent.service.SingleUseService;
import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.http.HttpConnectionCloser;
import com.flash3388.flashlib.vision.ImagePipeline;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.jpeg.reader.MjpegReader;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MjpegClient extends SingleUseService {

    private final URL mStreamUrl;
    private final ExecutorService mExecutorService;
    private final ImagePipeline<JpegImage> mImageConsumer;
    private final Logger mLogger;

    private final AtomicReference<HttpURLConnection> mConnectionReference;

    public MjpegClient(URL streamUrl, ExecutorService executorService, ImagePipeline<JpegImage> imageConsumer, Logger logger) {
        mStreamUrl = streamUrl;
        mExecutorService = executorService;
        mImageConsumer = imageConsumer;
        mLogger = logger;

        mConnectionReference = new AtomicReference<>();
    }

    public static MjpegClient create(URL url, ImagePipeline<JpegImage> imageConsumer, Logger logger) {
        return new MjpegClient(url, Executors.newSingleThreadExecutor(), imageConsumer, logger);
    }

    @Override
    protected void startRunning() {
        try {
            HttpURLConnection connection = (HttpURLConnection) mStreamUrl.openConnection();
            InputStream connectionInputStream = new BufferedInputStream(connection.getInputStream());

            mConnectionReference.set(connection);

            mExecutorService.submit(new MjpegReadTask(new MjpegReader(connectionInputStream), mImageConsumer, mLogger));
        } catch (IOException e) {
            mLogger.error("Failed to start", e);
        }
    }

    @Override
    protected void stopRunning() {
        mLogger.debug("Stopping MJPEG Client");

        try (Closer closer = Closer.empty()) {
            HttpURLConnection connection = mConnectionReference.getAndSet(null);
            if (connection != null) {
                closer.add(new HttpConnectionCloser(connection));
            }

            closer.add(new ExecutorCloser(mExecutorService));
        } catch (IOException e) {
            mLogger.error("Error while closing client", e);
        }
    }
}
