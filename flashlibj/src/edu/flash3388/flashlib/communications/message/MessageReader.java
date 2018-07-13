package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class MessageReader {

	private Connection mConnection;
	private PrimitiveSerializer mSerializer;

	private byte[] mLastHeaderData;
	
	public MessageReader(Connection connection, PrimitiveSerializer serializer) {
		mConnection = connection;
		mSerializer = serializer;
	}
	
	public Message readMessage() throws ReadException, TimeoutException {
		try {
			byte[] headerData;

			if (mLastHeaderData == null) {
				headerData = mConnection.read(8);
			} else {
				headerData = mLastHeaderData;
				mLastHeaderData = null;
			}

			int header = mSerializer.toInt(headerData);
			int length = mSerializer.toInt(headerData, 4);

			try {
				byte[] data = mConnection.read(length);
				return new StaticMessage(header, data);
			} catch (Throwable t) {
				mLastHeaderData = headerData;

				throw t;
			}
		} catch(IOException e) {
			throw new ReadException(e);
		}
	}
}
