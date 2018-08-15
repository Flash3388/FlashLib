package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSessionException;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.manager.messages.SendableMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SendableMessageHandler implements ManagerMessageHandler {

    private final SendableSessionManager mSendableSessionManager;
    private final Logger mLogger;

    public SendableMessageHandler(SendableSessionManager sendableSessionManager, Logger logger) {
        mSendableSessionManager = sendableSessionManager;
        mLogger = logger;
    }

    @Override
    public boolean canHandle(Message message) {
        return SendableMessage.HEADER == message.getHeader();
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        SendableMessage sendableMessage = SendableMessage.fromMessage(message);
        SendableData local = sendableMessage.getTo();
        Message messageToSendable = sendableMessage.getSendableMessage();

        try {
            mSendableSessionManager.newMessageForSession(local, messageToSendable);
        } catch (NoSuchSessionException ex) {
            mLogger.log(Level.SEVERE, "failed registering new message", ex);
        }
    }
}
