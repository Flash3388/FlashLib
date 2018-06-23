package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class MessageReader {

	private Connection mConnection;
	private PrimitiveSerializer mSerializer;
	
	public MessageReader(Connection connection, PrimitiveSerializer serializer) {
		mConnection = connection;
		mSerializer = serializer;
	}
	
	public Message readMessage() throws ReadException, TimeoutException {
		try {
			byte[] headerData = mConnection.read(4);
			int header = mSerializer.toInt(headerData);
			
			byte[] lengthData = mConnection.read(4);
			int length = mSerializer.toInt(lengthData);
			
			byte[] data = mConnection.read(length);
			return new StaticMessage(header, data);
		} catch(IOException e) {
			throw new ReadException(e);
		}
	}
}
