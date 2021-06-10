package com.flash3388.flashlib.vision;

import java.io.IOException;

public interface Image {

    int getHeight();
    int getWidth();

    boolean isEmpty();
    byte[] getRaw() throws IOException;

    java.awt.Image toAwt();
}
