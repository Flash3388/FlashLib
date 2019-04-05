package com.flash3388.flashlib.communication.connection;

import java.io.Closeable;
import java.io.IOException;

public interface Connector extends Closeable {

	Connection connect(int connectionTimeout) throws ConnectionFailedException, TimeoutException;
	
	@Override
	void close() throws IOException;
}
