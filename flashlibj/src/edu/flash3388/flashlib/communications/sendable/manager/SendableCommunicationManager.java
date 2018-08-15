package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.runner.CommunicationRunner;
import edu.flash3388.flashlib.communications.runner.events.ConnectionEvent;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.communications.runner.events.DisconnectionEvent;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.*;
import edu.flash3388.flashlib.communications.sendable.manager.listeners.ManagerMessageListener;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryRequestMessage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.ManagerMessagesPredicate;
import edu.flash3388.flashlib.event.TrueEventPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class SendableCommunicationManager {

    private final SendableStorage mSendableStorage;
    private final SendableSessionManager mSendableSessionManager;
    private final PairHandler mPairHandler;
    private final SendableHandler mSendableHandler;
    private final SendableMatcher mSendableMatcher;
    private final Logger mLogger;

    private final ConnectionListener mConnectionListener;
    private final ManagerMessageListener mMessageListener;

    private final Lock mLock;

    private final DiscoverySetting mDiscoverySetting;

    private CommunicationRunner mAttachedCommunicationRunner;
    private MessageQueue mMessageQueue;
    private boolean mIsRunning;
    private boolean mIsConnected;

    public SendableCommunicationManager(Logger logger, DiscoverySetting discoverySetting) {
        mLogger = logger;
        mDiscoverySetting = discoverySetting;

        mLock = new ReentrantLock();
        mSendableMatcher = new SendableMatcher();

        mSendableStorage = new SendableStorage(new SendableStreamFactory());
        mSendableSessionManager = new SendableSessionManager(mSendableStorage);

        mPairHandler = new PairHandler(mSendableSessionManager, mLogger);
        mSendableHandler = new SendableHandler(mSendableSessionManager);

        mConnectionListener = new ManagerConnectionListener(this);
        mMessageListener = new ManagerMessageListener(createMessageHandlers());

        mAttachedCommunicationRunner = null;
        mIsRunning = false;
        mIsConnected = false;
    }

    public boolean attachSendable(SendableData sendableData, Sendable sendable) {
        mLock.lock();
        try {
            if (mSendableStorage.addSendable(sendableData, sendable)) {
                if (mIsConnected) {
                    mSendableHandler.handleAttachedSendable(mDiscoverySetting, mMessageQueue);
                }

                return true;
            }

            return false;
        } finally {
            mLock.unlock();
        }
    }

    public boolean detachSendable(SendableData sendableData) {
        mLock.lock();
        try {
            if (mSendableStorage.removeSendable(sendableData)) {
                if (mIsConnected) {
                    mSendableHandler.handleDetachedSendable(sendableData, mMessageQueue);
                }

                return true;
            }

            return false;
        } finally {
            mLock.unlock();
        }
    }

    public void start(CommunicationRunner communicationRunner) {
        mLock.lock();
        try {
            if (mIsRunning) {
                throw new IllegalStateException("already started");
            }
            mIsRunning = true;

            mAttachedCommunicationRunner = communicationRunner;

            mAttachedCommunicationRunner.addMessageListener(new ManagerMessagesPredicate(), mMessageListener);
            mAttachedCommunicationRunner.addConnectionListener(new TrueEventPredicate(), mConnectionListener);
        } finally {
            mLock.unlock();
        }
    }

    public boolean isRunning() {
        mLock.lock();
        try {
            return mIsRunning;
        } finally {
            mLock.unlock();
        }
    }

    public void stop() {
        mLock.lock();
        try {
            if (!mIsRunning) {
                throw new IllegalStateException("not started");
            }
            mIsRunning = false;

            mAttachedCommunicationRunner.removeConnectionListener(mConnectionListener);
            mAttachedCommunicationRunner.removeMessageListener(mMessageListener);
        } finally {
            mLock.unlock();
        }
    }

    private void onCommunicationConnection(MessageQueue messageQueue) {
        mLock.lock();
        try {
            mIsConnected = true;
            mMessageQueue = messageQueue;

            if (mDiscoverySetting == DiscoverySetting.SEND_DISCOVERY_REQUESTS) {
                DiscoveryRequestMessage discoveryRequestMessage = new DiscoveryRequestMessage();
                messageQueue.enqueueMessage(discoveryRequestMessage);
            }
        } finally {
            mLock.unlock();
        }
    }

    private void onCommunicationDisconnection() {
        mLock.lock();
        try {
            mIsConnected = false;

            mSendableSessionManager.closeAllSessions();
        } finally {
            mLock.unlock();
        }
    }

    private Collection<ManagerMessageHandler> createMessageHandlers() {
        Collection<ManagerMessageHandler> messageHandlers = new ArrayList<ManagerMessageHandler>();
        messageHandlers.add(new DiscoveryRequestHandler(mSendableStorage));
        messageHandlers.add(new DiscoveryDataHandler(mSendableStorage, mSendableSessionManager, mSendableMatcher, mPairHandler, mLock));
        messageHandlers.add(new PairRequestHandler(mPairHandler, mLock));
        messageHandlers.add(new PairSuccessHandler(mPairHandler, mLock));
        messageHandlers.add(new SessionCloseHandler(mPairHandler, mLock));
        messageHandlers.add(new SendableMessageHandler(mSendableSessionManager, mLogger));

        return messageHandlers;
    }

    private static class ManagerConnectionListener implements ConnectionListener {

        private SendableCommunicationManager mCommunicationManager;

        ManagerConnectionListener(SendableCommunicationManager communicationManager) {
            mCommunicationManager = communicationManager;
        }

        @Override
        public void onConnection(ConnectionEvent e) {
            mCommunicationManager.onCommunicationConnection(e.getMessageQueue());
        }

        @Override
        public void onDisconnection(DisconnectionEvent e) {
            mCommunicationManager.onCommunicationDisconnection();
        }
    }
}
