package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.io.packing.DataBufferUnpacker;
import edu.flash3388.flashlib.io.packing.Packing;

public class MessageReader {

	private final Connection mConnection;

	private byte[] mLastHeaderData;
	
	public MessageReader(Connection connection) {
		mConnection = connection;
	}
	
	public Message readMessage() throws ReadException, TimeoutException {
		try {
			byte[] headerData;

			if (mLastHeaderData == null) {
				headerData = mConnection.read(4);
			} else {
				headerData = mLastHeaderData;
				mLastHeaderData = null;
			}

            DataBufferUnpacker unpacker = Packing.newBufferUnpacker(headerData);

			int header = unpacker.unpackInt();
			int length = unpacker.unpackInt();

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
