package edu.flash3388.flashlib.communications.connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {

    void write(int data) throws IOException;
    void write(byte[] data) throws IOException;
	void write(byte[] data, int start, int length) throws IOException;

	int read() throws IOException, TimeoutException;
	int read(byte[] bytes, int start, int length) throws IOException, TimeoutException;
	byte[] read(int count) throws IOException, TimeoutException;
	
	@Override
	void close() throws IOException;
}
