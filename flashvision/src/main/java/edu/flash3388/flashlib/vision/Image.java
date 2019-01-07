package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.vision.jpeg.JpegImage;

import java.io.IOException;

public interface Image {

    int getHeight();
    int getWidth();

    byte[] getRaw() throws IOException;

    JpegImage toJpeg();
}
