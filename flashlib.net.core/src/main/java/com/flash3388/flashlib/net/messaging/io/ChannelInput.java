package com.flash3388.flashlib.net.messaging.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class ChannelInput extends InputStream {

    private final WeakReference<TcpChannel> mChannel;
    private final ByteBuffer mBuffer;
    private int mIndex;
    private int mSize;

    public ChannelInput(TcpChannel channel, ByteBuffer buffer) {
        mChannel = new WeakReference<>(channel);
        mBuffer = buffer;
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
        return mBuffer.get(mIndex++);
    }

    private void updateBuffer() throws IOException {
        if (mIndex < mSize) {
            return;
        }

        TcpChannel channel = mChannel.get();
        if (channel == null) {
            throw new IllegalStateException("channel was garbage collected");
        }

        mSize = channel.read(mBuffer);
        mIndex = 0;
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedEncodingException();
    }
}
