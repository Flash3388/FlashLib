package edu.flash3388.flashlib.util.http;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpConnectionCloser implements Closeable {

    private final HttpURLConnection mConnection;

    public HttpConnectionCloser(HttpURLConnection connection) {
        mConnection = connection;
    }

    @Override
    public void close() throws IOException {
        mConnection.disconnect();
    }
}
