package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.communications.sendable.SendableStream;

public class SendableController {

    private SendableData mSendableData;
    private Sendable mSendable;
    private SendableStreamFactory mSendableStreamFactory;

    public SendableController(SendableData sendableData, Sendable sendable, SendableStreamFactory sendableStreamFactory) {
        mSendableData = sendableData;
        mSendable = sendable;
        mSendableStreamFactory = sendableStreamFactory;
    }

    public SendableSession startNewSession(SendableData to, MessageQueue messageQueue) {
        SendableStream sendableStream = mSendableStreamFactory.create(mSendableData, to, messageQueue);
        return mSendable.onPairing(sendableStream);
    }
}
