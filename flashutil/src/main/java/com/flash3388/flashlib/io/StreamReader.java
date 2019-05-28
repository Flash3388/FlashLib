package com.flash3388.flashlib.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamReader implements Closeable {

    private static final int BUFFER_SIZE = 1024;

    private final InputStream mInputStream;

    public StreamReader(InputStream inputStream) {
        mInputStream = inputStream;
    }

    public byte[] readAll() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int bytesRead;

            while ((bytesRead = mInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }

    @Override
    public void close() throws IOException {
        mInputStream.close();
    }
}
