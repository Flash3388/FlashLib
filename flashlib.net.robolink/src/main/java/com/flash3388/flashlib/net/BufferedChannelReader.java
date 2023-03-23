package com.flash3388.flashlib.net;

import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferedChannelReader extends InputStream {

    private final ConnectedNetChannel mChannel;
    private final ByteBuffer mBuffer;

    private int mIndex;
    private int mSize;

    public BufferedChannelReader(ConnectedNetChannel channel, ByteBuffer buffer) {
        mChannel = channel;
        mBuffer = buffer;

        mIndex = 0;
        mSize = 0;
    }

    public BufferedChannelReader(ConnectedNetChannel channel) {
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
            int available = mSize - mIndex;
            int wanted = len - off - read;
            int toCopy = Math.min(available, wanted);

            mBuffer.position(mIndex).get(b, read + off, toCopy).rewind();
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
        try {
            mSize = mChannel.read(mBuffer);
            mIndex = 0;
        } catch (TimeoutException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        // DO NOTHING
    }
}
