package com.flash3388.flashlib.net.channels.messsaging;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer mBuffer;
    private final int mMaxCapacity;
    private final int mStartingPosition;

    public ByteBufferOutputStream(ByteBuffer buffer, int maxCapacity) {
        mBuffer = buffer;
        mMaxCapacity = maxCapacity;
        mStartingPosition = buffer.position();
    }

    @Override
    public void write(int b) throws IOException {
        expandBufferIfNeeded(1);
        mBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        expandBufferIfNeeded(len - off);
        mBuffer.put(b, off, len);
    }

    public void write(ByteBuffer buffer) throws IOException {
        expandBufferIfNeeded(buffer.limit());
        mBuffer.put(buffer);
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public void revert() {
        mBuffer.position(mStartingPosition);
    }

    private void expandBufferIfNeeded(int amountWanted) throws IOException {
        if (mBuffer.remaining() >= amountWanted) {
            return;
        }

        int neededCapacity = mBuffer.capacity() + amountWanted; // TODO: ROUND TO 1024
        int newCapacity = Math.min(Math.max(mBuffer.capacity() * 2, neededCapacity), mMaxCapacity);
        if (newCapacity >= mMaxCapacity || newCapacity < neededCapacity) {
            throw new IOException("buffer has reached maximum allowed capacity");
        }

        ByteBuffer newBuffer = mBuffer.isDirect() ?
                ByteBuffer.allocateDirect(newCapacity) :
                ByteBuffer.allocate(newCapacity);
        mBuffer.flip();
        newBuffer.put(mBuffer);
        mBuffer = newBuffer;
    }
}
