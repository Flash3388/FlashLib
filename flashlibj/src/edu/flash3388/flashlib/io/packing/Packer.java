package edu.flash3388.flashlib.io.packing;

import java.nio.charset.Charset;

public class Packer {

    private final PackerBuffer mBuffer;
    private final Charset mCharset;

    public Packer(PackerBuffer buffer, Charset charset) {
        mBuffer = buffer;
        mCharset = charset;
    }

    public void packBoolean(boolean value) {
        mBuffer.write(value ? 1 : 0);
    }

    public void packByte(byte value) {
        mBuffer.write(value);
    }

    public void packShort(short value) {
        for (int i = 1; i >= 0; i--) {
            mBuffer.write(value & 0xff);
            value >>= 8;
        }
    }

    public void packInt(int value) {
        for (int i = 3; i >= 0; i--) {
            mBuffer.write(value & 0xff);
            value >>= 8;
        }
    }

    public void packLong(long value) {
        for (int i = 7; i >= 0; i--) {
            mBuffer.write((int) (value & 0xff));
            value >>= 8;
        }
    }

    public void packFloat(float value) {
        int intBits = Float.floatToIntBits(value);
        packInt(intBits);
    }

    public void packDouble(double value) {
        long longBits = Double.doubleToLongBits(value);
        packLong(longBits);
    }

    public void packByteArray(byte[] bytes, int start, int length) {
        packInt(length);
        mBuffer.write(bytes, start, length);
    }

    public void packByteArray(byte[] bytes) {
        packInt(bytes.length);
        mBuffer.write(bytes);
    }

    public void packString(String string) {
        byte[] bytes = string.getBytes(mCharset);
        packByteArray(bytes);
    }
}
