package com.flash3388.flashlib.vision.jpeg.server;

import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.flow.SingleUseRunner;
import com.flash3388.flashlib.util.http.HttpServerCloser;
import com.flash3388.flashlib.vision.camera.Camera;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class MjpegServer extends SingleUseRunner {

    private final HttpServer mServer;
    private final Clock mClock;
    private final Logger mLogger;

    private final Map<String, HttpContext> mContextMap;

    public MjpegServer(HttpServer server, Clock clock, Logger logger) {
        mServer = server;
        mClock = clock;
        mLogger = logger;

        mContextMap = new HashMap<>();
    }

    public static MjpegServer create(InetSocketAddress serverAddress, int handlerThreads, Clock clock, Logger logger) throws IOException {
        return new MjpegServer(HttpServer.create(serverAddress, handlerThreads), clock, logger);
    }

    public void setCamera(String name, Camera<JpegImage> camera) {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }


        if (mContextMap.containsKey(name)) {
            throw new IllegalStateException("Camera already set");
        } else {
            HttpContext context = mServer.createContext(name, new MjpegServerStreamHandler(camera, mClock, mLogger));
            mContextMap.put(name, context);
        }
    }

    @Override
    protected void startRunner() {
        mServer.start();
    }

    @Override
    protected void stopRunner() {
        try {
            Closer closer = Closer.empty();

            closer.add(new HttpServerCloser(mServer));

            closer.close();
        } catch (IOException e) {
            mLogger.error("Error while stopping server", e);
        }
    }
}
