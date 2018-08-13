package edu.flash3388.flashlib.communications.sendable.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableStream;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class MessageQueueSendableStream implements SendableStream {

    private MessageQueue mMessageQueue;
    private SendableData mFrom;
    private SendableData mTo;
    private PrimitiveSerializer mSerializer;

    public MessageQueueSendableStream(MessageQueue messageQueue, SendableData from, SendableData to, PrimitiveSerializer serializer) {
        mMessageQueue = messageQueue;
        mFrom = from;
        mTo = to;
        mSerializer = serializer;
    }

    @Override
    public void sendMessage(Message message) {
        SendableMessage sendableMessage = new SendableMessage(mFrom, mTo, message, mSerializer);
        mMessageQueue.enqueueMessage(sendableMessage);
    }
}
