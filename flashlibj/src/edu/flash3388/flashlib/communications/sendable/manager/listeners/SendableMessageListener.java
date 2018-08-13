package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSessionException;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.messages.SendableMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SendableMessageListener implements MessageListener {

    private SendableSessionManager mSendableSessionManager;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    public SendableMessageListener(SendableSessionManager sendableSessionManager, PrimitiveSerializer serializer, Logger logger) {
        mSendableSessionManager = sendableSessionManager;
        mSerializer = serializer;
        mLogger = logger;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        SendableMessage sendableMessage = SendableMessage.fromMessage(e.getMessage(), mSerializer);
        SendableData local = sendableMessage.getTo();
        Message message = sendableMessage.getSendableMessage();

        try {
            mSendableSessionManager.newMessageForSession(local, message);
        } catch (NoSuchSessionException ex) {
            mLogger.log(Level.SEVERE, "failed registering new message", ex);
        }
    }
}
