package edu.flash3388.flashlib.communications.message;

public class StaticMessage implements Message {

	private final int mHeader;
	private final byte[] mData;
	
	public StaticMessage(int header, byte[] data) {
		mHeader = header;
		mData = data;
	}
	
	@Override
	public int getHeader() {
		return mHeader;
	}

	@Override
	public byte[] getData() {
		return mData;
	}
}
