package edu.flash3388.flashlib.io.packing;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static edu.flash3388.flashlib.io.packing.DataCode.TRUE;

public class DataUnpacker implements Closeable {

    private final InputStream mInputStream;
    private final ByteBuffer mBuffer;
    private final Charset mCharset;

    public DataUnpacker(InputStream inputStream, ByteBuffer buffer, Charset charset) {
        mInputStream = inputStream;
        mBuffer = buffer;
        mCharset = charset;

        mBuffer.limit(0);
    }

    public boolean unpackBoolean() throws IOException {
        ensureAvailable(1);
        byte b = mBuffer.get();
        return b == TRUE;
    }

    public char unpackChar() throws IOException {
        ensureAvailable(2);
        return mBuffer.getChar();
    }

    public byte unpackByte() throws IOException {
        ensureAvailable(1);
        return mBuffer.get();
    }

    public short unpackShort() throws IOException {
        ensureAvailable(2);
        return mBuffer.getShort();
    }

    public int unpackInt() throws IOException {
        ensureAvailable(4);
        return mBuffer.getInt();
    }

    public long unpackLong() throws IOException {
        ensureAvailable(8);
        return mBuffer.getLong();
    }

    public float unpackFloat() throws IOException {
        ensureAvailable(4);
        return mBuffer.getFloat();
    }

    public double unpackDouble() throws IOException {
        ensureAvailable(8);
        return mBuffer.getDouble();
    }

    public byte[] unpackByteArray() throws IOException {
        int length = unpackInt();

        ensureAvailable(length);
        byte[] arr = new byte[length];
        mBuffer.get(arr);
        return arr;
    }

    public String unpackString() throws IOException {
        byte[] bytes = unpackByteArray();
        return new String(bytes, mCharset);
    }

    @Override
    public void close() throws IOException {
        mInputStream.close();
    }

    private void ensureAvailable(int bytes) throws IOException {
        if (mBuffer.remaining() < bytes) {
            readNext(bytes);
        }
    }

    private void readNext(int amount) throws IOException {
        byte[] bytes = new byte[amount];
        int read = mInputStream.read(bytes);

        mBuffer.position(0);
        mBuffer.limit(read);
        mBuffer.put(bytes, 0, read);
        mBuffer.position(0);
    }
}
