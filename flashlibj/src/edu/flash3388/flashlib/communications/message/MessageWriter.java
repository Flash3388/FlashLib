package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

public class MessageWriter {

	private Connection mConnection;
	private PrimitiveSerializer mSerializer;
	
	public MessageWriter(Connection connection, PrimitiveSerializer serializer) {
		mConnection = connection;
		mSerializer = serializer;
	}
	
	public void writeMessage(Message message) throws WriteException {
		byte[] header = mSerializer.toBytes(message.getHeader());
		byte[] data = message.getData();
		byte[] lengthData = mSerializer.toBytes(data.length);
		
		byte[] sendData = ArrayUtil.combine(header, lengthData, data);
		
		try {
			mConnection.write(sendData);
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}
}
