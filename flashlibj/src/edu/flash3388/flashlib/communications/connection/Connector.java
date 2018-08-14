package edu.flash3388.flashlib.communications.connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connector extends Closeable {

	Connection connect(int connectionTimeout) throws ConnectionFailedException;
	
	@Override
	void close() throws IOException;
}
