package edu.flash3388.flashlib.io.packing;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class Packing {

    private Packing() {}

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final int DEFAULT_CAPACITY = 1024;
    private static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    public static DataPacker newPacker(OutputStream outputStream) {
        return newPacker(outputStream, DEFAULT_CAPACITY);
    }

    public static DataPacker newPacker(OutputStream outputStream, int bufferSize) {
        return new DataPacker(outputStream, allocateBuffer(bufferSize), DEFAULT_CHARSET);
    }

    public static DataBufferPacker newBufferPacker() {
        return newBufferPacker(DEFAULT_CAPACITY);
    }

    public static DataBufferPacker newBufferPacker(int bufferSize) {
        return new DataBufferPacker(allocateBuffer(bufferSize), DEFAULT_CHARSET);
    }

    public static DataUnpacker newUnpacker(InputStream inputStream) {
        return newUnpacker(inputStream, DEFAULT_CAPACITY);
    }

    public static DataUnpacker newUnpacker(InputStream inputStream, int bufferSize) {
        return new DataUnpacker(inputStream, allocateBuffer(bufferSize), DEFAULT_CHARSET);
    }

    public static DataBufferUnpacker newBufferUnpacker(byte[] bytes) {
        return new DataBufferUnpacker(bytes, allocateBuffer(bytes.length), DEFAULT_CHARSET);
    }

    private static ByteBuffer allocateBuffer(int capacity) {
        return ByteBuffer.allocate(capacity).order(DEFAULT_BYTE_ORDER);
    }
}
