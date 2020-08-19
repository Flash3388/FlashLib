package com.flash3388.flashlib.vision.jpeg.server;

import com.castle.concurrent.service.SingleUseService;
import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.http.HttpServerCloser;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MjpegServer extends SingleUseService {

    private final HttpServer mServer;
    private final ExecutorService mExecutorService;
    private final Clock mClock;
    private final Logger mLogger;

    private final Map<String, HttpContext> mContextMap;

    public MjpegServer(HttpServer server, ExecutorService executorService, Clock clock, Logger logger) {
        mServer = server;
        mExecutorService = executorService;
        mClock = clock;
        mLogger = logger;

        mContextMap = new HashMap<>();
    }

    public static MjpegServer create(InetSocketAddress serverAddress, int handlerThreads, Clock clock, Logger logger) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(handlerThreads);
        HttpServer server = HttpServer.create(serverAddress, handlerThreads);
        server.setExecutor(executorService);

        return new MjpegServer(server, executorService, clock, logger);
    }

    public void setCamera(String name, Camera<JpegImage> camera) {
        if (!name.startsWith("/")) {
            name = "/".concat(name);
        }

        if (mContextMap.containsKey(name)) {
            throw new IllegalStateException("Camera already set");
        } else {
            HttpContext context = mServer.createContext(name, new MjpegServerStreamHandler(camera, mClock, mLogger));
            mContextMap.put(name, context);
        }
    }

    @Override
    protected void startRunning() {
        mServer.start();
    }

    @Override
    protected void stopRunning() {
        mLogger.debug("Closing MJPEG Server");
        try (Closer closer = Closer.empty()){
            closer.add(new ExecutorCloser(mExecutorService));
            closer.add(new HttpServerCloser(mServer));
        } catch (Exception e) {
            mLogger.error("Error while stopping server", e);
        }
    }
}
