package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.communications.message.Message;

public interface Sendable {

	void onReceive(Message message);
	
	void onConnect(SendableMessageWriter stream);
	void onDisconnect();
}
