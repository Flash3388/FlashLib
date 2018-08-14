package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.communications.message.Message;


public interface SendableSession {

    void onMessageReceived(Message message);
    void close();
}
