package com.flash3388.flashlib.util.http;

import com.flash3388.flashlib.time.Time;
import com.sun.net.httpserver.HttpServer;

import java.io.Closeable;
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
    public void close() {
        mServer.stop((int) mStopDelay.toUnit(TimeUnit.SECONDS).value());
    }
}
