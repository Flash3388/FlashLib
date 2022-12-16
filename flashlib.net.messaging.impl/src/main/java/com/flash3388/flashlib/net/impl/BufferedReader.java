package com.flash3388.flashlib.net.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferedReader extends InputStream {

    private final ReadableChannel mChannel;
    private final ByteBuffer mBuffer;

    private int mIndex;
    private int mSize;

    public BufferedReader(ReadableChannel channel, ByteBuffer buffer) {
        mChannel = channel;
        mBuffer = buffer;

        mIndex = 0;
        mSize = 0;
    }

    public BufferedReader(ReadableChannel channel) {
        this(channel, ByteBuffer.allocateDirect(1024));
    }

    public void clear() {
        mBuffer.clear();
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
