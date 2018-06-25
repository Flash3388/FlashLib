package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.communications.message.Message;

public interface SendableMessageWriter {

	void write(Message message);
}
