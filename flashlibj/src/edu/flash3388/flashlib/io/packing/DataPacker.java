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

    public DataPacker(OutputStream outputStream, ByteBuffer buffer, Charset charset) {
        mOutputStream = outputStream;
        mBuffer = buffer;
        mCharset = charset;
    }

    public DataPacker packBoolean(boolean value) throws IOException {
        ensureCapacity(1);
        mBuffer.put(value ? TRUE : FALSE);
        return this;
    }

    public DataPacker packChar(char value) throws IOException {
        ensureCapacity(2);
        mBuffer.putChar(value);
        return this;
    }

    public DataPacker packByte(byte value) throws IOException {
        ensureCapacity(1);
        mBuffer.put(value);
        return this;
    }

    public DataPacker packShort(short value) throws IOException {
        ensureCapacity(2);
        mBuffer.putShort(value);
        return this;
    }

    public DataPacker packInt(int value) throws IOException {
        ensureCapacity(4);
        mBuffer.putInt(value);
        return this;
    }

    public DataPacker packLong(long value) throws IOException {
        ensureCapacity(8);
        mBuffer.putLong(value);
        return this;
    }

    public DataPacker packFloat(float value) throws IOException {
        ensureCapacity(4);
        mBuffer.putFloat(value);
        return this;
    }

    public DataPacker packDouble(double value) throws IOException {
        ensureCapacity(8);
        mBuffer.putDouble(value);
        return this;
    }

    public DataPacker packByteArray(byte[] bytes, int start, int length) throws IOException {
        packInt(length);
        ensureCapacity(length);
        mBuffer.put(bytes, start, length);
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
        if (mBuffer.position() > 0) {
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

    private void ensureCapacity(int bytes) throws IOException {
        if (mBuffer.position() + bytes >= mBuffer.capacity()) {
            flushBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        byte[] buffer = mBuffer.array();
        mOutputStream.write(buffer, 0, mBuffer.position());
        mBuffer.position(0);
    }
}
