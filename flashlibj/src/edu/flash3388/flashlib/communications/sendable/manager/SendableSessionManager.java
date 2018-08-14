package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.util.Pair;

import java.util.*;

public class SendableSessionManager {

    private SendableStorage mSendableStorage;
    private Map<SendableData, SendableSessionData> mSendableSessions;

    public SendableSessionManager(SendableStorage sendableStorage) {
        mSendableStorage = sendableStorage;
        mSendableSessions = new HashMap<SendableData, SendableSessionData>();
    }


    public synchronized boolean hasSession(SendableData sendableData) {
        return mSendableSessions.containsKey(sendableData);
    }

    public synchronized Optional<SendableData> getSessionRemote(SendableData sendableData) {
        if (hasSession(sendableData)) {
            try {
                SendableSessionData sendableSession = getSendableSession(sendableData);
                return Optional.of(sendableSession.getRemote());
            } catch (NoSuchSessionException e) {
                // would not happen
                throw new RuntimeException(e);
            }
        }

        return Optional.empty();
    }

    public synchronized Collection<Pair<SendableData, SendableData>> getSendablesPairedWithRemotes(Collection<SendableData> remotes) {
        Collection<Pair<SendableData, SendableData>> sendableDataCollection = new ArrayList<Pair<SendableData, SendableData>>();

        for (SendableSessionData sendableSessionData : mSendableSessions.values()) {
            if (remotes.contains(sendableSessionData.getRemote())) {
                sendableDataCollection.add(Pair.create(sendableSessionData.getLocal(), sendableSessionData.getRemote()));
            }
        }

        return sendableDataCollection;
    }

    public synchronized void startNewSendableSession(SendableData sendableData, SendableData to, MessageQueue messageQueue) throws SessionAlreadyExistsException, NoSuchSendableException {
        if (mSendableSessions.containsKey(sendableData)) {
            throw new SessionAlreadyExistsException();
        }

        SendableController sendableController = mSendableStorage.getControllerForSendable(sendableData);
        SendableSession sendableSession = sendableController.startNewSession(to, messageQueue);
        mSendableSessions.put(sendableData, new SendableSessionData(sendableData, to, sendableSession));
    }

    public void newMessageForSession(SendableData sendableData, Message message) throws NoSuchSessionException {
        SendableSessionData sendableSession = getSendableSession(sendableData);

        synchronized (sendableSession) {
            sendableSession.getSession().onMessageReceived(message);
        }
    }

    public void closeSendableSession(SendableData sendableData) throws NoSuchSessionException {
        SendableSessionData sendableSession = getSendableSession(sendableData);

        synchronized (sendableSession) {
            sendableSession.getSession().close();

            synchronized (this) {
                mSendableSessions.remove(sendableData);
            }
        }
    }

    public synchronized void closeAllSessions() {
        for (SendableData sendableData : mSendableSessions.keySet()) {
            try {
                closeSendableSession(sendableData);
            } catch (NoSuchSessionException e) {
                // should not happen
                throw new RuntimeException(e);
            }
        }
    }

    private SendableSessionData getSendableSession(SendableData sendableData) throws NoSuchSessionException {
        SendableSessionData sendableSession;

        synchronized (this) {
            sendableSession = mSendableSessions.get(sendableData);
        }

        if (sendableSession == null) {
            throw new NoSuchSessionException();
        }

        return sendableSession;
    }
}
