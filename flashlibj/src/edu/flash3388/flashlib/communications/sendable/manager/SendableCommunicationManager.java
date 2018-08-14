package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.runner.CommunicationRunner;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.DiscoveryHandler;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.PairHandler;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class SendableCommunicationManager {

    private SendableStorage mSendableStorage;
    private SendableSessionManager mSendableSessionManager;
    private PairHandler mPairHandler;
    private DiscoveryHandler mDiscoveryHandler;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    private AtomicBoolean mIsConnected;

    public SendableCommunicationManager(PrimitiveSerializer serializer, Logger logger) {
        mSerializer = serializer;
        mLogger = logger;

        mSendableStorage = new SendableStorage(new SendableStreamFactory(mSerializer));
        mSendableSessionManager = new SendableSessionManager(mSendableStorage);
        mPairHandler = new PairHandler(mSendableSessionManager, mSerializer, mLogger);
        mDiscoveryHandler = new DiscoveryHandler();

        mIsConnected = new AtomicBoolean(false);
    }

    public boolean attachSendable(SendableData sendableData, Sendable sendable) {
        if (mSendableStorage.addSendable(sendableData, sendable)) {
            // TODO: IF CONNECTED, SEND DISCOVERY, OR CONNECT

            return true;
        }

        return false;
    }

    public boolean detachSendable(SendableData sendableData) {
        if (mSendableStorage.removeSendable(sendableData)) {
            // TODO: IF CONNECTED, UPDATE DISCOVERY, DISCONNECT

            return true;
        }

        return false;
    }

    public void start(CommunicationRunner communicationRunner) {

    }

    public void stop() {

    }
}
