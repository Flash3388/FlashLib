package edu.flash3388.flashlib.communications.message;

import java.io.IOException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.io.packing.DataBufferPacker;
import edu.flash3388.flashlib.io.packing.Packing;
import edu.flash3388.flashlib.util.ArrayUtil;

public class MessageWriter {

	private final Connection mConnection;
	
	public MessageWriter(Connection connection) {
		mConnection = connection;
	}
	
	public void writeMessage(Message message) throws WriteException {
		try {
            byte[] data = message.getData();
            DataBufferPacker packer = Packing.newBufferPacker(data.length + 4);

            packer.packInt(message.getHeader());
            packer.packInt(data.length);
            packer.close();

            byte[] headerData = packer.toByteArray();
            mConnection.write(headerData);
            mConnection.write(data);
		} catch (IOException e) {
			throw new WriteException(e);
		}
	}
}
