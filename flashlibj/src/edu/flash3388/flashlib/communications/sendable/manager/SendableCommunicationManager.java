package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.runner.CommunicationRunner;
import edu.flash3388.flashlib.communications.runner.events.ConnectionEvent;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.communications.runner.events.DisconnectionEvent;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.PairHandler;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.SendableHandler;
import edu.flash3388.flashlib.event.Event;
import edu.flash3388.flashlib.event.Listener;
import edu.flash3388.flashlib.event.TrueEventPredicate;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class SendableCommunicationManager {

    private final SendableStorage mSendableStorage;
    private final SendableSessionManager mSendableSessionManager;
    private final RemoteSendablesStatus mRemoteSendablesStatus;
    private final PairHandler mPairHandler;
    private final SendableHandler mSendableHandler;
    private final PrimitiveSerializer mSerializer;
    private final Logger mLogger;

    private final ConnectionListener mConnectionListener;
    private final Collection<Pair<Predicate<Event>, MessageListener>> mRunnerListeners;

    private CommunicationRunner mAttachedCommunicationRunner;
    private boolean mIsRunning;
    private boolean mIsConnected;

    public SendableCommunicationManager(PrimitiveSerializer serializer, Logger logger) {
        mSerializer = serializer;
        mLogger = logger;

        mSendableStorage = new SendableStorage(new SendableStreamFactory(mSerializer));
        mSendableSessionManager = new SendableSessionManager(mSendableStorage);
        mRemoteSendablesStatus = new RemoteSendablesStatus();

        mPairHandler = new PairHandler(mSendableSessionManager, mSerializer, mLogger);
        mSendableHandler = new SendableHandler(mSendableSessionManager, mRemoteSendablesStatus);

        mConnectionListener = new ManagerConnectionListener(this);
        mRunnerListeners = new ArrayList<Pair<Predicate<Event>, MessageListener>>();

        mAttachedCommunicationRunner = null;
        mIsRunning = false;
        mIsConnected = false;
    }

    public synchronized boolean attachSendable(SendableData sendableData, Sendable sendable) {
        if (mSendableStorage.addSendable(sendableData, sendable)) {
            if (mIsConnected) {
                mSendableHandler.handleAttachedSendable(sendableData);// TODO: IF CONNECTED, SEND DISCOVERY, OR CONNECT
            }

            return true;
        }

        return false;
    }

    public synchronized boolean detachSendable(SendableData sendableData) {
        if (mSendableStorage.removeSendable(sendableData)) {
            if (mIsConnected) {
                mSendableHandler.handleDetachedSendable(sendableData); // TODO: IF CONNECTED, UPDATE DISCOVERY, DISCONNECT
            }

            return true;
        }

        return false;
    }

    public synchronized void start(CommunicationRunner communicationRunner) {
        if (mIsRunning) {
            throw new IllegalStateException("already started");
        }
        mIsRunning = true;

        for (Pair<Predicate<Event>, MessageListener> listenerData : mRunnerListeners) {
            communicationRunner.addMessageListener(listenerData.getKey(), listenerData.getValue());
        }

        mAttachedCommunicationRunner.addConnectionListener(new TrueEventPredicate(), mConnectionListener);

        mAttachedCommunicationRunner = communicationRunner;
    }

    public synchronized boolean isRunning() {
        return mIsRunning;
    }

    public synchronized void stop() {
        if (!mIsRunning) {
            throw new IllegalStateException("not started");
        }
        mIsRunning = false;

        mAttachedCommunicationRunner.removeConnectionListener(mConnectionListener);

        for (Pair<Predicate<Event>, MessageListener> listenerData : mRunnerListeners) {
            mAttachedCommunicationRunner.removeMessageListener(listenerData.getValue());
        }
    }

    private synchronized void onCommunicationConnection() {
        mIsConnected = true;


    }

    private synchronized void onCommunicationDisconnection() {
        mIsConnected = false;

        mSendableSessionManager.closeAllSessions();
        mRemoteSendablesStatus.reset();
    }

    private static class ManagerConnectionListener implements ConnectionListener {

        private SendableCommunicationManager mCommunicationManager;

        ManagerConnectionListener(SendableCommunicationManager communicationManager) {
            mCommunicationManager = communicationManager;
        }

        @Override
        public void onConnection(ConnectionEvent e) {
            mCommunicationManager.onCommunicationConnection();
        }

        @Override
        public void onDisconnection(DisconnectionEvent e) {
            mCommunicationManager.onCommunicationDisconnection();
        }
    }
}
