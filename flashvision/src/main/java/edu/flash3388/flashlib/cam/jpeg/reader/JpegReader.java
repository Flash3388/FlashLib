package edu.flash3388.flashlib.cam.jpeg.reader;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;

import java.io.Closeable;
import java.io.IOException;

public interface JpegReader extends Closeable {

    JpegImage read() throws IOException;

    @Override
    void close() throws IOException;
}
