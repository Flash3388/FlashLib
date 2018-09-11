package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.SendableMatcher;
import edu.flash3388.flashlib.communications.sendable.manager.SendableSessionManager;
import edu.flash3388.flashlib.communications.sendable.manager.SendableStorage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryDataMessage;
import edu.flash3388.flashlib.util.Pair;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

public class DiscoveryDataHandler implements ManagerMessageHandler {

    private final SendableStorage mSendableStorage;
    private final SendableSessionManager mSendableSessionManager;
    private final SendableMatcher mSendableMatcher;
    private final PairHandler mPairHandler;
    private final Lock mManagerLock;

    public DiscoveryDataHandler(SendableStorage sendableStorage, SendableSessionManager sendableSessionManager, SendableMatcher sendableMatcher, PairHandler pairHandler,
                                Lock managerLock) {
        mSendableStorage = sendableStorage;
        mSendableSessionManager = sendableSessionManager;
        mSendableMatcher = sendableMatcher;
        mPairHandler = pairHandler;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return message.getHeader() == DiscoveryDataMessage.HEADER;
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        DiscoveryDataMessage discoveryDataMessage = DiscoveryDataMessage.fromMessage(message);
        Collection<SendableData> sendableDataCollection = discoveryDataMessage.getSendables();

        mManagerLock.lock();
        try {
            if (discoveryDataMessage.getState() == DiscoveryDataMessage.State.ATTACHED) {
                pairSendables(sendableDataCollection, messageQueue);
            } else {
                unpairSendables(sendableDataCollection, messageQueue);
            }
        } finally {
            mManagerLock.unlock();
        }
    }

    private void pairSendables(Collection<SendableData> sendableDataCollection, MessageQueue messageQueue) {
        for (SendableData local : mSendableStorage.getAllSendables()) {
            if (mSendableSessionManager.hasSession(local)) {
                continue;
            }

            SendableData matchedRemote = null;
            for (SendableData remote : sendableDataCollection) {
                if (mSendableMatcher.doMatch(local, remote)) {
                    mPairHandler.pair(local, remote, messageQueue);
                    matchedRemote = remote;
                    break;
                }
            }

            if (matchedRemote != null) {
                sendableDataCollection.remove(matchedRemote);
            }
        }
    }

    private void unpairSendables(Collection<SendableData> sendableDataCollection, MessageQueue messageQueue) {
        Collection<Pair<SendableData, SendableData>> localSendables = mSendableSessionManager.getSendablesPairedWithRemotes(sendableDataCollection);
        for (Pair<SendableData, SendableData> sendablePair : localSendables) {
            mPairHandler.unpair(sendablePair.getFirst(), sendablePair.getSecond(), messageQueue);
        }
    }
}
