package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSessionException;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.manager.messages.SessionCloseMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Optional;

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
        Optional<SendableData> optionalRemote = mSendableSessionMananger.getSessionRemote(sendableData);
        if (optionalRemote.isPresent()) {
            SendableData remote = optionalRemote.get();
            try {
                mSendableSessionMananger.closeSendableSession(sendableData);

                SessionCloseMessage sessionCloseMessage = new SessionCloseMessage(sendableData, remote, mSerializer);
                // TODO: SEND
            } catch (NoSuchSessionException e) {
                // should not occur
            }
        }
    }
}
