package com.flash3388.flashlib.net.obsr;

import com.beans.BooleanProperty;
import com.beans.properties.atomic.AtomicBooleanProperty;
import com.castle.concurrent.service.ServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.ConnectionListener;
import com.flash3388.flashlib.net.messaging.EmptyEvent;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.MessengerService;
import com.flash3388.flashlib.net.messaging.NewClientEvent;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ObsrService extends ServiceBase implements ObjectStorage {

    private static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");
    private static final long MESSENGER_ID = 555;

    private final InstanceId mOurId;
    private final Clock mClock;
    private final BooleanProperty mIsConnected;

    private Context mContext;

    public ObsrService(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;
        mIsConnected = new AtomicBooleanProperty(false);
    }

    public void configurePrimary(ChannelUpdater channelUpdater, SocketAddress bindAddress) {
        if (isRunning()) {
            throw new IllegalStateException("running");
        }

        NetworkedContext context = new NetworkedContext(mIsConnected, channelUpdater, mOurId, mClock, LOGGER);
        context.configurePrimary(bindAddress);
        mContext = context;
    }

    public void configureSecondary(ChannelUpdater channelUpdater, SocketAddress serverAddress) {
        if (isRunning()) {
            throw new IllegalStateException("running");
        }

        NetworkedContext context = new NetworkedContext(mIsConnected, channelUpdater, mOurId, mClock, LOGGER);
        context.configureSecondary(serverAddress);
        mContext = context;
    }

    public void configureLocal() {
        if (isRunning()) {
            throw new IllegalStateException("running");
        }

        mContext = new LocalContext(mClock, LOGGER);
    }

    @Override
    public boolean isLocal() {
        if (!isRunning()) {
            return true;
        }

        return !mIsConnected.getAsBoolean();
    }

    @Override
    public StoredObject getRoot() {
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        Storage storage = mContext.getStorage();
        return storage.getObject(StoragePath.root());
    }

    @Override
    public StoredObject getInstanceRoot() {
        return getRoot().getChild("instances").getChild(mOurId.toString());
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mContext == null) {
            throw new ServiceException("not configured");
        }

        mIsConnected.setAsBoolean(false);
        mContext.start();
    }

    @Override
    protected void stopRunning() {
        mContext.stop();
        mIsConnected.setAsBoolean(false);
    }

    private static Set<MessageType> getUsedMessageTypes() {
        return new HashSet<>(Arrays.asList(
                EntryChangeMessage.TYPE,
                StorageContentsMessage.TYPE
        ));
    }

    private interface Context {

        Storage getStorage();

        void start() throws ServiceException;
        void stop();
    }

    private static class NetworkedContext implements Context {

        private final MessengerService mMessenger;
        private final Storage mStorage;

        private NetworkedContext(BooleanProperty isConnected,
                                 ChannelUpdater channelUpdater,
                                 InstanceId ourId,
                                 Clock clock,
                                 Logger logger) {
            mMessenger = new MessengerService(channelUpdater, new ChannelId(ourId, MESSENGER_ID), clock);

            StorageListener storageListener = new StorageListenerImpl(mMessenger);
            EventController eventController = Controllers.newSyncExecutionController();
            mStorage = new StorageImpl(storageListener, eventController, clock, logger);

            Set<MessageType> messageTypes = getUsedMessageTypes();
            mMessenger.registerMessageTypes(messageTypes);
            mMessenger.addListener(new ConnectionListenerImpl(mMessenger, mStorage, logger, isConnected));
            mMessenger.addListener(new MessageListenerImpl(mStorage), messageTypes);
        }

        public void configurePrimary(SocketAddress bindAddress) {
            mMessenger.configureServer(bindAddress);
        }

        public void configureSecondary(SocketAddress serverAddress) {
            mMessenger.configureClient(serverAddress);
        }

        @Override
        public Storage getStorage() {
            return mStorage;
        }

        @Override
        public void start() throws ServiceException {
            mMessenger.start();
        }

        @Override
        public void stop() {
            mMessenger.stop();
        }
    }

    private static class LocalContext implements Context {

        private final Storage mStorage;

        private LocalContext(Clock clock, Logger logger) {
            StorageListener storageListener = new EmptyStorageListener();
            EventController eventController = Controllers.newSyncExecutionController();
            mStorage = new StorageImpl(storageListener, eventController, clock, logger);
        }

        @Override
        public Storage getStorage() {
            return mStorage;
        }

        @Override
        public void start() throws ServiceException {

        }

        @Override
        public void stop() {

        }
    }

    private static class ConnectionListenerImpl implements ConnectionListener {

        private final Messenger mMessenger;
        private final Storage mStorage;
        private final Logger mLogger;
        private final BooleanProperty mIsConnected;

        private int mConnectedClients;

        private ConnectionListenerImpl(Messenger messenger,
                                       Storage storage,
                                       Logger logger,
                                       BooleanProperty isConnected) {
            mMessenger = messenger;
            mStorage = storage;
            mLogger = logger;
            mIsConnected = isConnected;

            mConnectedClients = 0;
        }

        @Override
        public void onConnected(EmptyEvent event) {
            mLogger.debug("OBSR connected");
            mIsConnected.setAsBoolean(true);
        }

        @Override
        public void onDisconnected(EmptyEvent event) {
            mLogger.debug("OBSR disconnected");
            mIsConnected.setAsBoolean(false);
        }

        @Override
        public void onClientConnected(NewClientEvent event) {
            mLogger.debug("New client connected to OBSR, sending storage contents");
            mIsConnected.setAsBoolean(true);
            mConnectedClients++;

            Message message = mStorage.createContentsMessage();
            mMessenger.send(message);
        }

        @Override
        public void onClientDisconnected(NewClientEvent event) {
            mConnectedClients--;
            if (mConnectedClients < 1) {
                mIsConnected.setAsBoolean(false);
            }
        }
    }

    private static class MessageListenerImpl implements MessageListener {

        private final Storage mStorage;

        private MessageListenerImpl(Storage storage) {
            mStorage = storage;
        }

        @Override
        public void onNewMessage(NewMessageEvent event) {
            mStorage.updateFromMessage(event);
        }
    }
}
