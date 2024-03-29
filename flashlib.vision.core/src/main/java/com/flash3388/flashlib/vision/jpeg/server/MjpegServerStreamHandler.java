package com.flash3388.flashlib.vision.jpeg.server;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class MjpegServerStreamHandler implements HttpHandler {

    private static final String BOUNDARY = "--boundary";
    private static final String HEADER_FORMAT = "\r\n\r\n%s\r\nContent-Type: image/jpeg\r\nContent-Length:%d\r\n\r\n";

    private final WeakReference<Source<? extends JpegImage>> mCameraReference;
    private final int mFps;
    private final Clock mClock;
    private final Logger mLogger;

    public MjpegServerStreamHandler(Source<? extends JpegImage> camera,
                                    int fps,
                                    Clock clock,
                                    Logger logger) {
        mCameraReference = new WeakReference<>(camera);
        mFps = fps;
        mClock = clock;
        mLogger = logger;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Headers h = httpExchange.getResponseHeaders();
        h.set("Cache-Control", "no-cache, private");
        h.set("Content-Type", "multipart/x-mixed-replace;boundary=" + BOUNDARY);
        httpExchange.sendResponseHeaders(200, 0);

        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            streamImages(outputStream);
        }
    }

    private void streamImages(OutputStream outputStream) throws IOException {
        while (!Thread.interrupted()) {
            try {
                Source<? extends JpegImage> camera = mCameraReference.get();
                if (camera == null) {
                    mLogger.info("Camera collected by GC");
                    return;
                }

                Time startTime = mClock.currentTime();

                JpegImage image = camera.get();
                byte[] imageBytes = image.getRaw();

                outputStream.write(String.format(HEADER_FORMAT, BOUNDARY, imageBytes.length).getBytes());
                outputStream.write(imageBytes);
                outputStream.flush();

                Time timeTaken = mClock.currentTime().sub(startTime);
                long sleepMillis = timeTaken.valueAsMillis() - (1000 / mFps);

                if (sleepMillis > 0) {
                    Thread.sleep(sleepMillis);
                }
            } catch (InterruptedException e) {
                break;
            } catch (VisionException e) {
                mLogger.error("Error while writing stream", e);
            } catch (IOException e) {
                mLogger.error("Error while writing stream, stopping", e);
                break;
            }
        }
    }
}
