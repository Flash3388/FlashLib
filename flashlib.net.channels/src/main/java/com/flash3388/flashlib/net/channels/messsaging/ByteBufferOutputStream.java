package com.flash3388.flashlib.net.channels.messsaging;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer mBuffer;

    public ByteBufferOutputStream(ByteBuffer buffer) {
        mBuffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        mBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        mBuffer.put(b, off, len);
    }
}
