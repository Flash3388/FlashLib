package edu.flash3388.flashlib.communications.connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {

	void write(byte[] data) throws IOException;
	byte[] read(int count) throws IOException, TimeoutException;
	
	@Override
	void close() throws IOException;
}
