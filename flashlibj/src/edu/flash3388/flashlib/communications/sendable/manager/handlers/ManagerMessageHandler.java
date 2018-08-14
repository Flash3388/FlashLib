package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;

public interface ManagerMessageHandler {

    boolean canHandle(Message message);
    void handle(Message message, MessageQueue messageQueue);
}
