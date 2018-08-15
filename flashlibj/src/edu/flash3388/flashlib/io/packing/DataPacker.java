package edu.flash3388.flashlib.io.packing;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static edu.flash3388.flashlib.io.packing.DataCode.FALSE;
import static edu.flash3388.flashlib.io.packing.DataCode.TRUE;

public class DataPacker implements Closeable, Flushable {

    protected final OutputStream mOutputStream;
    private final ByteBuffer mBuffer;
    private final Charset mCharset;
    private int mPosition;

    public DataPacker(OutputStream outputStream, ByteBuffer buffer, Charset charset) {
        mOutputStream = outputStream;
        mBuffer = buffer;
        mCharset = charset;
        mPosition = 0;
    }

    private void ensureCapacity(int bytes) throws IOException {
        if (mPosition + bytes >= mBuffer.capacity()) {
            flushBuffer();
        }
    }

    public DataPacker packBoolean(boolean value) throws IOException {
        ensureCapacity(1);
        mBuffer.put(mPosition++, value ? TRUE : FALSE);
        return this;
    }

    public DataPacker packByte(byte value) throws IOException {
        ensureCapacity(1);
        mBuffer.put(mPosition++, value);
        return this;
    }

    public DataPacker packShort(short value) throws IOException {
        ensureCapacity(2);
        mBuffer.putShort(mPosition, value);
        mPosition += 2;
        return this;
    }

    public DataPacker packInt(int value) throws IOException {
        ensureCapacity(4);
        mBuffer.putInt(mPosition, value);
        mPosition += 4;
        return this;
    }

    public DataPacker packLong(long value) throws IOException {
        ensureCapacity(8);
        mBuffer.putLong(mPosition, value);
        mPosition += 8;
        return this;
    }

    public DataPacker packFloat(float value) throws IOException {
        ensureCapacity(4);
        mBuffer.putFloat(mPosition, value);
        mPosition += 4;
        return this;
    }

    public DataPacker packDouble(double value) throws IOException {
        ensureCapacity(8);
        mBuffer.putDouble(mPosition, value);
        mPosition += 8;
        return this;
    }

    public DataPacker packByteArray(byte[] bytes, int start, int length) throws IOException {
        packInt(length);
        ensureCapacity(length);
        mBuffer.put(bytes, start, length);
        mPosition += length;
        return this;
    }

    public DataPacker packByteArray(byte[] bytes) throws IOException {
        return packByteArray(bytes, 0, bytes.length);
    }

    public DataPacker packString(String string) throws IOException {
        byte[] bytes = string.getBytes(mCharset);
        return packByteArray(bytes);
    }

    @Override
    public void flush() throws IOException {
        if (mPosition > 0) {
            flushBuffer();
        }
        mOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            flush();
        } finally {
            mOutputStream.close();
        }
    }

    private void flushBuffer() throws IOException {
        byte[] buffer = mBuffer.array();
        mOutputStream.write(buffer);
        mBuffer.reset();
        mPosition = 0;
    }
}
