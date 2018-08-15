package edu.flash3388.flashlib.io.packing;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Packing {

    private Packing() {}

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final int DEFAULT_CAPACITY = 1024;

    public static DataPacker newPacker(OutputStream outputStream) {
        return new DataPacker(outputStream, allocateBuffer(), DEFAULT_CHARSET);
    }

    public static DataBufferPacker newBufferPacker() {
        return new DataBufferPacker(allocateBuffer(), DEFAULT_CHARSET);
    }

    public static DataUnpacker newUnpacker(InputStream inputStream) {
        return new DataUnpacker(inputStream, allocateBuffer(), DEFAULT_CHARSET);
    }

    public static DataBufferUnpacker newBufferUnpacker(byte[] bytes) {
        return new DataBufferUnpacker(bytes, allocateBuffer(), DEFAULT_CHARSET);
    }

    private static ByteBuffer allocateBuffer() {
        return ByteBuffer.allocate(DEFAULT_CAPACITY);
    }
}
