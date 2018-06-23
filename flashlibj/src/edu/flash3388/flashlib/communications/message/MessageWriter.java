package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.util.FlashUtil;

public class MessageWriter {

	private Connection mConnection;
	
	public MessageWriter(Connection connection) {
		mConnection = connection;
	}
	
	public void writeMessage(Message message) throws WriteException {
		byte[] header = FlashUtil.toByteArray(message.getHeader());
		byte[] data = message.getData();
		byte[] lengthData = FlashUtil.toByteArray(data.length);
		
		byte[] sendData = new byte[header.length + 4 + data.length];
		System.arraycopy(header, 0, sendData, 0, header.length);
		System.arraycopy(lengthData, 0, sendData, header.length, 4);
		System.arraycopy(data, 0, sendData, header.length + 4, data.length);
		
		try {
			mConnection.write(sendData);
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}
}
