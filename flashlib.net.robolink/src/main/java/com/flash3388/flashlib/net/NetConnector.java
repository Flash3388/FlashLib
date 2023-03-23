package com.flash3388.flashlib.net;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.time.Time;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public interface NetConnector extends Closeable {

    ConnectedNetChannel connect(SocketAddress remote, Time timeout) throws IOException, TimeoutException, InterruptedException;

    @Override
    void close();
}
