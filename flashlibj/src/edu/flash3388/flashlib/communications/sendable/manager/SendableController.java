package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.communications.sendable.messages.MessageQueueSendableStream;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class SendableController {

    private SendableData mSendableData;
    private Sendable mSendable;
    private PrimitiveSerializer mSerializer;

    public SendableController(SendableData sendableData, Sendable sendable, PrimitiveSerializer serializer) {
        mSendableData = sendableData;
        mSendable = sendable;
        mSerializer = serializer;
    }

    public SendableSession startNewSession(SendableData to, MessageQueue messageQueue) {
        return mSendable.onPairing(new MessageQueueSendableStream(messageQueue, mSendableData, to, mSerializer));
    }
}
