package edu.flash3388.flashlib.communications.connection;

public interface Connector {

	Connection connect(int connectionTimeout) throws ConnectionFailedException;
}
