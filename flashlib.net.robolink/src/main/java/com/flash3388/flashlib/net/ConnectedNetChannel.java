package com.flash3388.flashlib.net;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface ConnectedNetChannel extends Closeable {

    void write(ByteBuffer buffer) throws IOException, InterruptedException;
    int read(ByteBuffer buffer) throws IOException, TimeoutException, InterruptedException;

    @Override
    void close();
}
