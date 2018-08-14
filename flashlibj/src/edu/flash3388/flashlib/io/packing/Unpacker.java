package edu.flash3388.flashlib.io.packing;

import java.nio.charset.Charset;

public class Unpacker {

    private final UnpackerBuffer mBuffer;
    private final Charset mCharset;

    public Unpacker(UnpackerBuffer buffer, Charset charset) {
        mBuffer = buffer;
        mCharset = charset;
    }

    public boolean unpackBoolean() {
        return mBuffer.read() == 1;
    }

    public byte unpackByte() {
        return (byte) mBuffer.read();
    }

    public short unpackShort() {
        short result = 0;

        for (int i = 0; i < 2; i++) {
            result <<= 8;
            result |= (mBuffer.read() & 0xff);
        }

        return result;
    }

    public int unpackInt() {
        int result = 0;

        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (mBuffer.read() & 0xff);
        }

        return result;
    }

    public long unpackLong() {
        long result = 0;

        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (mBuffer.read() & 0xff);
        }

        return result;
    }

    public float unpackFloat() {
        int intBits = unpackInt();
        return Float.intBitsToFloat(intBits);
    }

    public double unpackDouble() {
        long longBits = unpackLong();
        return Double.longBitsToDouble(longBits);
    }

    public byte[] unpackByteArray() {
        int length = unpackInt();

        byte[] arr = new byte[length];
        mBuffer.read(arr);
        return arr;
    }

    public String unpackString() {
        byte[] bytes = unpackByteArray();
        return new String(bytes, mCharset);
    }
}
