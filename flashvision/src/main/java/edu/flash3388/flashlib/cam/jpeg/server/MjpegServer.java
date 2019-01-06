package edu.flash3388.flashlib.cam.jpeg.server;

import com.sun.net.httpserver.HttpServer;
import edu.flash3388.flashlib.cam.jpeg.JpegCamera;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.util.concurrent.ExecutorCloser;
import edu.flash3388.flashlib.util.http.HttpServerCloser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MjpegServer {

    private final HttpServer mServer;
    private final ExecutorService mExecutorService;
    private final Clock mClock;
    private final Logger mLogger;

    public MjpegServer(HttpServer server, ExecutorService executorService, Clock clock, Logger logger) {
        mServer = server;
        mExecutorService = executorService;
        mClock = clock;
        mLogger = logger;

        mServer.setExecutor(mExecutorService);
    }

    public MjpegServer create(InetSocketAddress serverAddress, int handlerThreads, Clock clock, Logger logger) throws IOException {
        return new MjpegServer(HttpServer.create(serverAddress, handlerThreads), Executors.newFixedThreadPool(handlerThreads), clock, logger);
    }

    public void start() {
        mServer.start();
    }

    public void setCamera(String name, JpegCamera camera) {
        mServer.createContext(name, new MjpegServerStreamHandler(camera, mClock, mLogger));
    }

    public void terminate() {
        try {
            Closer closer = Closer.empty();

            closer.add(new ExecutorCloser(mExecutorService));
            closer.add(new HttpServerCloser(mServer));

            closer.close();
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "error while stopping mjpeg server", e);
        }
    }
}
