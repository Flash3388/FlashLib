package edu.flash3388.flashlib.io.packing;

import java.nio.charset.Charset;

public class Packing {

    private Packing() {}

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static Packer newDefaultPacker(PackerBuffer buffer) {
        return new Packer(buffer, DEFAULT_CHARSET);
    }

    public static Unpacker newDefaultUnpacker(UnpackerBuffer buffer) {
        return new Unpacker(buffer, DEFAULT_CHARSET);
    }
}
