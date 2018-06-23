package edu.flash3388.flashlib.communications.connection;

import java.io.IOException;

public interface Connection {

	void write(byte[] data) throws IOException;
	byte[] read(int count) throws IOException, TimeoutException;
}
