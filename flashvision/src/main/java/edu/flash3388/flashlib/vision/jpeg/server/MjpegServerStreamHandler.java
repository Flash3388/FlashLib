package edu.flash3388.flashlib.vision.jpeg.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;
import edu.flash3388.flashlib.vision.camera.Camera;
import edu.flash3388.flashlib.vision.VisionException;
import edu.flash3388.flashlib.vision.jpeg.JpegImage;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MjpegServerStreamHandler implements HttpHandler {

    private static final String BOUNDARY = "--boundary";
    private static final String HEADER_FORMAT = "\r\n\r\n%s\r\nContent-Type: image/jpeg\r\nContent-Length:%d\r\n\r\n";

    private final WeakReference<Camera<JpegImage>> mCameraReference;
    private final Clock mClock;
    private final Logger mLogger;

    public MjpegServerStreamHandler(Camera<JpegImage> camera, Clock clock, Logger logger) {
        mCameraReference = new WeakReference<>(camera);
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
                Camera<JpegImage> camera = mCameraReference.get();
                if (camera == null) {
                    mLogger.log(Level.INFO, "Camera was collected by gc.");
                    return;
                }

                Time startTime = mClock.currentTime();

                JpegImage image = camera.capture();
                byte[] imageBytes = image.getRaw();

                outputStream.write(String.format(HEADER_FORMAT, BOUNDARY, imageBytes.length).getBytes());
                outputStream.write(imageBytes);
                outputStream.flush();

                Time timeTaken = mClock.currentTime().sub(startTime);
                long sleepMillis = timeTaken.getAsMillis() - (1000 / camera.getFps());

                if (sleepMillis > 0) {
                    Thread.sleep(sleepMillis);
                }
            } catch (InterruptedException e) {
                break;
            } catch (IOException | VisionException e) {
                mLogger.log(Level.SEVERE, "Error in stream handler", e);
            }
        }
    }
}
