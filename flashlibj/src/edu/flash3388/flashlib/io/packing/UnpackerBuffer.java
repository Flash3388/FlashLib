package edu.flash3388.flashlib.io.packing;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class UnpackerBuffer {

    private final ByteArrayInputStream mInputStream;

    private UnpackerBuffer(ByteArrayInputStream inputStream) {
        mInputStream = inputStream;
    }

    public int read() {
        return mInputStream.read();
    }

    public int read(byte[] bytes, int start, int length) {
        return mInputStream.read(bytes, start, length);
    }

    public int read(byte[] bytes) {
        try {
            return mInputStream.read(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UnpackerBuffer newDefaultBuffer(byte[] bytes) {
        return new UnpackerBuffer(new ByteArrayInputStream(bytes));
    }
}
