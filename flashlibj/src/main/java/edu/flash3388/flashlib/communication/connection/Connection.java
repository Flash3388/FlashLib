package edu.flash3388.flashlib.communication.connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {

	void write(byte[] data, int start, int length) throws IOException;

    default void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    int read(byte[] bytes, int start, int length) throws IOException, TimeoutException;

	default byte[] read(int count) throws IOException, TimeoutException {
        byte[] buffer = new byte[count];
        read(buffer, 0, count);
        return buffer;
    }
	
	@Override
	void close() throws IOException;
}
