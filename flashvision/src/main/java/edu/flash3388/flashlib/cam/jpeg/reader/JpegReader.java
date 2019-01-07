package edu.flash3388.flashlib.cam.jpeg.reader;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;

import java.io.IOException;

public interface JpegReader {

    JpegImage read() throws IOException;
}
