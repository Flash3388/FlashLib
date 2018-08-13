package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableStream;
import edu.flash3388.flashlib.communications.sendable.messages.MessageQueueSendableStream;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class SendableStreamFactory {

    private PrimitiveSerializer mSerializer;

    public SendableStreamFactory(PrimitiveSerializer serializer) {
        mSerializer = serializer;
    }

    public SendableStream create(SendableData from, SendableData to, MessageQueue messageQueue) {
        return new MessageQueueSendableStream(messageQueue, from, to, mSerializer);
    }
}
