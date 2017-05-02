package edu.flash3388.flashlib.communications;

public interface SendableData {
	byte[] get();
	boolean hasChanged();
	void onConnection();
	void onConnectionLost();
}
