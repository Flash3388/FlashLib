package edu.flash3388.flashlib.io.packing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PackerBuffer {

    private final ByteArrayOutputStream mOutputStream;

    private PackerBuffer(ByteArrayOutputStream outputStream) {
        mOutputStream = outputStream;
    }

    public void write(int value) {
        mOutputStream.write(value);
    }

    public void write(byte[] bytes, int start, int length) {
        mOutputStream.write(bytes, start, length);
    }

    public void write(byte[] bytes) {
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        mOutputStream.reset();
    }

    public byte[] toByteArray() {
        return mOutputStream.toByteArray();
    }

    public static PackerBuffer newDefaultBuffer() {
        return new PackerBuffer(new ByteArrayOutputStream());
    }
}
