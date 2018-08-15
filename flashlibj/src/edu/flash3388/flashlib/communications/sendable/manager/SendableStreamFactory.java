package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableStream;
import edu.flash3388.flashlib.communications.sendable.messages.MessageQueueSendableStream;

public class SendableStreamFactory {

    public SendableStream create(SendableData from, SendableData to, MessageQueue messageQueue) {
        return new MessageQueueSendableStream(messageQueue, from, to);
    }
}
