package edu.flash3388.flashlib.vision.jpeg.reader;

import edu.flash3388.flashlib.vision.jpeg.JpegImage;

import java.io.IOException;

public interface JpegReader {

    JpegImage read() throws IOException;
}
