package edu.flash3388.flashlib.vision.jpeg.reader;

import edu.flash3388.flashlib.communication.connection.Connection;

import java.io.IOException;

public interface JpegReaderFactory {

    JpegReader create(Connection connection) throws IOException;
}