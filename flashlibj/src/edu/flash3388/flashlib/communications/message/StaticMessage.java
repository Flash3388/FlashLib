package edu.flash3388.flashlib.communications.message;

public class StaticMessage implements Message {

	private int mHeader;
	private byte[] mData;
	
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
