package com.flash3388.flashlib.communication.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FakeConnection implements Connection {

    private final ByteBuffer mBuffer;

    public FakeConnection(ByteBuffer buffer) {
        mBuffer = buffer;
    }

    @Override
    public void write(byte[] data, int start, int length) throws IOException {
        mBuffer.put(data, start, length);
    }

    @Override
    public int read(byte[] bytes, int start, int length) throws IOException, TimeoutException {
        mBuffer.get(bytes, start, length);
        return length;
    }

    @Override
    public void close() throws IOException {
    }
}
