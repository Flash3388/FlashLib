package com.flash3388.flashlib.net.messaging.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferedReader extends InputStream {

    private final TcpSocketChannel mChannel;
    private final ByteBuffer mBuffer;

    private int mIndex;
    private int mSize;

    public BufferedReader(TcpSocketChannel channel) {
        mChannel = channel;
        mBuffer = ByteBuffer.allocateDirect(1024);

        mIndex = 0;
        mSize = 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = 0;
        while (read < len) {
            updateBuffer();
            int available = mSize - mIndex - 1;
            int wanted = len - off - read;
            int toCopy = Math.min(available, wanted);

            System.arraycopy(mBuffer.array(), mIndex, b, read + off, toCopy);
            mIndex += toCopy;
            read += toCopy;
        }

        return read;
    }

    @Override
    public int read() throws IOException {
        updateBuffer();
        byte b = mBuffer.get(mIndex++);
        return b & 0xff;
    }

    private void updateBuffer() throws IOException {
        if (mIndex < mSize) {
            return;
        }
        mSize = mChannel.read(mBuffer);
        mIndex = 0;
    }

    @Override
    public void close() throws IOException {
        // DO NOTHING
    }
}
