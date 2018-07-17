package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.runner.events.ConnectionEvent;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.communications.runner.events.DisconnectionEvent;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.beans.Property;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

class CommunicationConnectionListener implements ConnectionListener {

    private PrimitiveSerializer mSerializer;
    private Property<MessageWriter> mMessageWriterProperty;
    private Runnable mDiscoveryCallable;
    private Runnable mResetCallable;
    private AtomicBoolean mIsConnected;
    private Logger mLogger;

    CommunicationConnectionListener(PrimitiveSerializer serializer, Property<MessageWriter> messageWriterProperty,
                                    Runnable discoveryCallable, Runnable resetCallable,
                                    AtomicBoolean isConnected, Logger logger) {
        mSerializer = serializer;
        mMessageWriterProperty = messageWriterProperty;
        mDiscoveryCallable = discoveryCallable;
        mResetCallable = resetCallable;
        mIsConnected = isConnected;
        mLogger = logger;
    }

    @Override
    public void onConnection(ConnectionEvent e) {
        mIsConnected.set(true);
        MessageWriter messageWriter = new MessageWriter(e.getConnection(), mSerializer);
        mMessageWriterProperty.setValue(messageWriter);
        mLogger.info("Connected");
        mDiscoveryCallable.run();
    }

    @Override
    public void onDisconnection(DisconnectionEvent e) {
        mIsConnected.set(false);
        mMessageWriterProperty.setValue(null);
        mLogger.info("Disconnected");
        mResetCallable.run();
    }
}
