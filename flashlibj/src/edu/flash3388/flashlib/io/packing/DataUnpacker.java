package edu.flash3388.flashlib.io.packing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static edu.flash3388.flashlib.io.packing.DataCode.TRUE;

public class DataUnpacker {

    private final InputStream mInputStream;
    private final ByteBuffer mBuffer;
    private final Charset mCharset;
    private int mPosition;

    public DataUnpacker(InputStream inputStream, ByteBuffer buffer, Charset charset) {
        mInputStream = inputStream;
        mBuffer = buffer;
        mCharset = charset;
        mPosition = 0;
    }

    private void ensureAvailable(int bytes) throws IOException {
        if (mBuffer.remaining() < bytes) {
            readNext(bytes);
        }
    }

    private void readNext(int amount) throws IOException {
        byte[] bytes = new byte[amount];
        int read = mInputStream.read(bytes);

        mBuffer.reset();
        mBuffer.limit(amount);
        mBuffer.put(bytes, 0, read);
        mPosition = 0;
    }

    public boolean unpackBoolean() throws IOException {
        ensureAvailable(1);
        byte b = mBuffer.get(mPosition++);
        return b == TRUE;
    }

    public byte unpackByte() throws IOException {
        ensureAvailable(1);
        return mBuffer.get(mPosition++);
    }

    public short unpackShort() throws IOException {
        ensureAvailable(2);
        short result = mBuffer.getShort(mPosition);
        mPosition += 2;
        return result;
    }

    public int unpackInt() throws IOException {
        ensureAvailable(4);
        int result = mBuffer.getInt(mPosition);
        mPosition += 4;
        return result;
    }

    public long unpackLong() throws IOException {
        ensureAvailable(8);
        long result = mBuffer.getLong(mPosition);
        mPosition += 8;
        return result;
    }

    public float unpackFloat() throws IOException {
        ensureAvailable(4);
        float result = mBuffer.getFloat(mPosition);
        mPosition += 4;
        return result;
    }

    public double unpackDouble() throws IOException {
        ensureAvailable(8);
        double result = mBuffer.getDouble(mPosition);
        mPosition += 8;
        return result;
    }

    public byte[] unpackByteArray() throws IOException {
        int length = unpackInt();

        byte[] arr = new byte[length];
        mBuffer.get(arr);
        mPosition += length;
        return arr;
    }

    public String unpackString() throws IOException {
        byte[] bytes = unpackByteArray();
        return new String(bytes, mCharset);
    }
}
