package edu.flash3388.flashlib.communications.sendable;

public class SendableData {

	private int mId;
	private int mType;
	private Sendable mSendable;
	
	public SendableData(Sendable sendable, int id, int type) {
		mSendable = sendable;
		mId = id;
		mType = type;
	}
	
	public Sendable getSendable() {
		return mSendable;
	}
	
	public int getID() {
		return mId;
	}
	
	public int getType() {
		return mType;
	}
}
