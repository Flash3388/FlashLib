package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.WriteException;

public interface SendableStream {

    void sendMessage(Message message) throws WriteException;
}
