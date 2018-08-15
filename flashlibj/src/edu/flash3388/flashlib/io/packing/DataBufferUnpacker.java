package edu.flash3388.flashlib.io.packing;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class DataBufferUnpacker extends DataUnpacker {

    public DataBufferUnpacker(byte[] bytes, ByteBuffer buffer, Charset charset) {
        super(new ByteArrayInputStream(bytes), buffer, charset);
    }
}
