package edu.flash3388.flashlib.util.http;

import com.sun.net.httpserver.HttpServer;
import edu.flash3388.flashlib.time.Time;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpServerCloser implements Closeable {

    private static final long STOP_DELAY_TIME_SECONDS = 1;

    private final HttpServer mServer;
    private final Time mStopDelay;

    public HttpServerCloser(HttpServer server, Time stopDelay) {
        mServer = server;
        mStopDelay = stopDelay;
    }

    public HttpServerCloser(HttpServer server) {
        this(server, Time.seconds(STOP_DELAY_TIME_SECONDS));
    }

    @Override
    public void close() throws IOException {
        mServer.stop((int) mStopDelay.getAsUnit(TimeUnit.SECONDS).getValue());
    }
}
