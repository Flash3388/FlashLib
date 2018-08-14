package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSessionException;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairCloseMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class SendableHandler {

    private final SendableSessionManager mSendableSessionMananger;
    private final PrimitiveSerializer mSerializer;

    public SendableHandler(SendableSessionManager sendableSessionManager, PrimitiveSerializer serializer) {
        mSendableSessionMananger = sendableSessionManager;
        mSerializer = serializer;
    }

    public void handleAttachedSendable(SendableData sendableData) {
        // TODO: send discovery if needed
    }

    public void handleDetachedSendable(SendableData sendableData) {
        try {
            SendableData remote = mSendableSessionMananger.getSessionRemote(sendableData);
            mSendableSessionMananger.closeSendableSession(sendableData);

            PairCloseMessage pairCloseMessage = new PairCloseMessage(sendableData, remote, mSerializer);
            // TODO: SEND
        } catch (NoSuchSessionException e) {
            // nothing we need to do
        }
    }
}
