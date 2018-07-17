package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Map;
import java.util.logging.Logger;

class SendableMessageListener implements MessageListener {

    private Map<Integer, ConnectedSendableSession> mSendableSessions;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    SendableMessageListener(Map<Integer, ConnectedSendableSession> sendableSessions, PrimitiveSerializer serializer, Logger logger) {
        mSendableSessions = sendableSessions;
        mSerializer = serializer;
        mLogger = logger;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        SendableMessage message = SendableMessage.fromMessage(e.getMessage(), mSerializer);

        int localId = message.getTo().getId();
        if (!mSendableSessions.containsKey(localId)) {
            mLogger.warning("received message for sendable with no session");
            return;
        }

        ConnectedSendableSession sendableSession = mSendableSessions.get(localId);
        Message sendableMessage = message.getSendableMessage();

        sendableSession.getSession().onMessageReceived(sendableMessage);
    }
}
