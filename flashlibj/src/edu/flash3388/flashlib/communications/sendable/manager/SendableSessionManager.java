package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;

import java.util.HashMap;
import java.util.Map;

public class SendableSessionManager {

    private SendableStorage mSendableStorage;
    private Map<SendableData, SendableSession> mSendableSessions;

    public SendableSessionManager(SendableStorage sendableStorage) {
        mSendableStorage = sendableStorage;
        mSendableSessions = new HashMap<SendableData, SendableSession>();
    }

    public synchronized void startNewSendableSession(SendableData sendableData, SendableData to, MessageQueue messageQueue) throws SessionAlreadyExistsException, NoSuchSendableException {
        if (mSendableSessions.containsKey(sendableData)) {
            throw new SessionAlreadyExistsException();
        }

        SendableController sendableController = mSendableStorage.getControllerForSendable(sendableData);
        SendableSession sendableSession = sendableController.startNewSession(to, messageQueue);
        mSendableSessions.put(sendableData, sendableSession);
    }

    public void newMessageForSession(SendableData sendableData, Message message) throws NoSuchSessionException {
        SendableSession sendableSession = getSendableSession(sendableData);

        synchronized (sendableSession) {
            sendableSession.onMessageReceived(message);
        }
    }

    public void closeSendableSession(SendableData sendableData) throws NoSuchSessionException {
        SendableSession sendableSession = getSendableSession(sendableData);

        synchronized (sendableSession) {
            sendableSession.close();
        }
    }

    private SendableSession getSendableSession(SendableData sendableData) throws NoSuchSessionException {
        SendableSession sendableSession;

        synchronized (this) {
            sendableSession = mSendableSessions.get(sendableData);
        }

        if (sendableSession == null) {
            throw new NoSuchSessionException();
        }

        return sendableSession;
    }
}
