package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSendableException;
import edu.flash3388.flashlib.communications.sendable.manager.NoSuchSessionException;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.manager.SessionAlreadyExistsException;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairFailureMessage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairSuccessMessage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.SessionCloseMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PairHandler {

    private SendableSessionManager mSendableSessionManager;
    private Logger mLogger;

    public PairHandler(SendableSessionManager sendableSessionManager, Logger logger) {
        mSendableSessionManager = sendableSessionManager;
        mLogger = logger;
    }

    public void pair(SendableData local, SendableData remote, MessageQueue messageQueue) {
        try {
            mSendableSessionManager.startNewSendableSession(local, remote, messageQueue);
            messageQueue.enqueueMessage(new PairSuccessMessage(local, remote));
        } catch (SessionAlreadyExistsException | NoSuchSendableException e) {
            mLogger.log(Level.SEVERE, "failed creating sendable session", e);
            messageQueue.enqueueMessage(new PairFailureMessage(local, remote));
        }
    }

    public void unpair(SendableData local, SendableData remote, MessageQueue messageQueue) {
        unpairWithoutResponse(local);
        messageQueue.enqueueMessage(new SessionCloseMessage(local, remote));
    }

    public void unpairWithoutResponse(SendableData local) {
        try {
            mSendableSessionManager.closeSendableSession(local);
        } catch (NoSuchSessionException e) {
            mLogger.log(Level.SEVERE, "failed closing sendable session", e);
        }
    }
}
