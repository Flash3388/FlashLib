package edu.flash3388.flashlib.vision;

import java.io.IOException;

public interface Image {

    int getHeight();
    int getWidth();

    byte[] getRaw() throws IOException;

    Image toJpeg();
}
