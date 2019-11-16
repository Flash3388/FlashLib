package com.flash3388.flashlib.vision.jpeg.client;

import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.flow.SingleUseThrowingParameterizedRunner;
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

public class MjpegClient extends SingleUseThrowingParameterizedRunner<ImagePipeline<JpegImage>, IOException> {

    private final URL mStreamUrl;
    private final ExecutorService mExecutorService;
    private final Logger mLogger;

    private final AtomicReference<HttpURLConnection> mConnectionReference;

    public MjpegClient(URL streamUrl, ExecutorService executorService, Logger logger) {
        mStreamUrl = streamUrl;
        mExecutorService = executorService;
        mLogger = logger;

        mConnectionReference = new AtomicReference<>();
    }

    public static MjpegClient create(URL url, Logger logger) {
        return new MjpegClient(url, Executors.newSingleThreadExecutor(), logger);
    }

    @Override
    protected void startRunner(ImagePipeline<JpegImage> imageConsumer) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) mStreamUrl.openConnection();
        InputStream connectionInputStream = new BufferedInputStream(connection.getInputStream());

        mConnectionReference.set(connection);

        mExecutorService.submit(new MjpegReadTask(new MjpegReader(connectionInputStream), imageConsumer, mLogger));
    }

    @Override
    protected void stopRunner() {
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
