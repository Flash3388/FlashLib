package edu.flash3388.flashlib.io.packing;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class DataBufferPacker extends DataPacker {

    public DataBufferPacker(ByteBuffer buffer, Charset charset) {
        super(new ByteArrayOutputStream(), buffer, charset);
    }

    public byte[] toByteArray() {
        return ((ByteArrayOutputStream)mOutputStream).toByteArray();
    }
}
