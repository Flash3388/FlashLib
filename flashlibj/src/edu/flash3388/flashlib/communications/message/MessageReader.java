package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import com.tomtzook.antman.communication.StaticMessage;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.util.FlashUtil;

public class MessageReader {

	private Connection mConnection;
	
	public MessageReader(Connection connection) {
		mConnection = connection;
	}
	
	public Message readMessage() throws ReadException, TimeoutException {
		try {
			byte[] headerData = mConnection.read(4);
			int header = FlashUtil.toInt(headerData);
			
			byte[] lengthData = mConnection.read(4);
			int length = FlashUtil.toInt(lengthData);
			
			byte[] data = mConnection.read(length);
			return new StaticMessage(header, data);
		} catch(IOException e) {
			throw new ReadException(e);
		}
	}
}
