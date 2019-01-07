package edu.flash3388.flashlib.cam.jpeg;

public class MjpegFormat {

    private MjpegFormat() {}

    public static final String CONTENT_LENGTH = "Content-Length";

    public static final byte[] SOI_MARKER = {(byte) 0xff, (byte) 0xd8};
}
