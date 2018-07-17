package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.event.MessageHeaderPredicate;
import edu.flash3388.flashlib.communications.runner.CommunicationRunner;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.event.TrueEventPredicate;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.SimpleProperty;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static edu.flash3388.flashlib.communications.sendable.manager.SendableMessageHeader.*;

public class SendableManager implements Closeable {

    private Map<Integer, AttachedSendable> mSendables;
    private Map<Integer, ConnectedSendableSession> mSendableSessions;
    private PrimitiveSerializer mSerializer;
    private Property<MessageWriter> mMessageWriterProperty;
    private AtomicBoolean mIsConnected;
    private Logger mLogger;

    private SendableConnector mSendableConnector;
    private SendableDisconnector mSendableDisconnector;
    private SendablePairRequestSender mSendablePairRequestSender;
    private SendableDiscoveryRunner mSendableDiscoveryRunner;
    private SendableConnectionsReseter mSendableConnectionReseter;

    private CommunicationConnectionListener mCommunicationConnectionListener;
    private SendableMessageListener mSendableMessageListener;
    private SendableConnectionListener mSendableConnectionListener;
    private SendableDisconnectionListener mSendableDisconnectionListener;

    public SendableManager(PrimitiveSerializer serializer, Logger logger) {
        mSerializer = serializer;
        mLogger = logger;

        mSendables = new ConcurrentHashMap<Integer, AttachedSendable>();
        mSendableSessions = new ConcurrentHashMap<Integer, ConnectedSendableSession>();
        mMessageWriterProperty = new SimpleProperty<MessageWriter>();
        mIsConnected = new AtomicBoolean(false);

        mSendableConnector = new SendableConnector(mSendables, mSendableSessions, mMessageWriterProperty, mSerializer, mLogger);
        mSendableDisconnector = new SendableDisconnector(mSendableSessions, mMessageWriterProperty, mSerializer, mLogger);
        mSendablePairRequestSender = new SendablePairRequestSender(mSerializer, mMessageWriterProperty, mLogger);
        mSendableDiscoveryRunner = new SendableDiscoveryRunner(mSendables, mSendablePairRequestSender);
        mSendableConnectionReseter = new SendableConnectionsReseter(mSendables, mSendableDisconnector);

        mCommunicationConnectionListener = new CommunicationConnectionListener(mSerializer, mMessageWriterProperty,
                mSendableDiscoveryRunner, mSendableConnectionReseter, mIsConnected, mLogger);
        mSendableMessageListener = new SendableMessageListener(mSendableSessions, mSerializer, mLogger);
        mSendableConnectionListener = new SendableConnectionListener(mSendableConnector, mSerializer);
        mSendableDisconnectionListener = new SendableDisconnectionListener(mSendableDisconnector, mSerializer);
    }

    public void attachSendable(SendableData sendableData, Sendable sendable, PairDiscoveryOption pairDiscoveryOption) {
        int id = sendableData.getId();

        if (mSendables.containsKey(id)) {
            // TODO: EXISTS
        }

        AttachedSendable attachedSendable = new AttachedSendable(sendableData, sendable, pairDiscoveryOption);
        mSendables.put(id, attachedSendable);

        if (mIsConnected.get() && pairDiscoveryOption == PairDiscoveryOption.SEND_PAIR_REQUESTS) {
            mSendablePairRequestSender.accept(sendableData);
        }
    }

    public void detachSendable(SendableData sendableData) {
        int id = sendableData.getId();

        if (!mSendables.containsKey(id)) {
            // TODO: DOESN'T EXISTS
        }

        mSendableDisconnector.accept(sendableData);
        mSendables.remove(id);
    }

    public void registerWithRunner(CommunicationRunner runner) {
        runner.addConnectionListener(new TrueEventPredicate(), mCommunicationConnectionListener);
        runner.addMessageListener(new MessageHeaderPredicate(SENDABLE_MESSAGE_HEADER), mSendableMessageListener);
        runner.addMessageListener(new MessageHeaderPredicate(SENDABLE_CONNECTION_HEADER), mSendableConnectionListener);
        runner.addMessageListener(new MessageHeaderPredicate(SENDABLE_DISCONNECTION_HEADER), mSendableDisconnectionListener);
    }

    public void unregisterWithRunner(CommunicationRunner runner) {
        runner.removeConnectionListener(mCommunicationConnectionListener);
        runner.removeMessageListener(mSendableMessageListener);
        runner.removeMessageListener(mSendableConnectionListener);
        runner.removeMessageListener(mSendableDisconnectionListener);

        mSendableConnectionReseter.run();
    }

    @Override
    public void close() throws IOException {
        mSendableConnectionReseter.run();
    }
}
