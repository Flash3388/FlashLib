package com.flash3388.flashlib.net.obsr;

import com.castle.concurrent.service.ServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.messaging.ConnectionListener;
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

    private final InstanceId mOurId;
    private final MessengerService mMessenger;
    private final Storage mStorage;

    public ObsrService(ChannelUpdater channelUpdater, InstanceId ourId, Clock clock) {
        mOurId = ourId;

        mMessenger = new MessengerService(channelUpdater, ourId, clock);

        StorageListener storageListener = new StorageListenerImpl(mMessenger);
        EventController eventController = Controllers.newSyncExecutionController();
        mStorage = new StorageImpl(storageListener, eventController, clock, LOGGER);

        Set<MessageType> messageTypes = getUsedMessageTypes();
        mMessenger.registerMessageTypes(messageTypes);
        mMessenger.addListener(new ConnectionListenerImpl(mMessenger, mStorage));
        mMessenger.addListener(new MessageListenerImpl(mStorage), messageTypes);
    }

    public void configurePrimary(SocketAddress bindAddress) {
        mMessenger.configureServer(bindAddress);
    }

    public void configureSecondary(SocketAddress serverAddress) {
        mMessenger.configureClient(serverAddress);
    }

    @Override
    public StoredObject getRoot() {
        return mStorage.getObject(StoragePath.root());
    }

    @Override
    public StoredObject getInstanceRoot() {
        return getRoot().getChild("instances").getChild(mOurId.toString());
    }

    @Override
    protected void startRunning() throws ServiceException {
        mMessenger.start();
    }

    @Override
    protected void stopRunning() {
        mMessenger.stop();
    }

    private static Set<MessageType> getUsedMessageTypes() {
        return new HashSet<>(Arrays.asList(
                EntryChangeMessage.TYPE,
                StorageContentsMessage.TYPE
        ));
    }

    private static class ConnectionListenerImpl implements ConnectionListener {

        private final Messenger mMessenger;
        private final Storage mStorage;

        private ConnectionListenerImpl(Messenger messenger, Storage storage) {
            mMessenger = messenger;
            mStorage = storage;
        }

        @Override
        public void onClientConnected(NewClientEvent event) {
            Message message = mStorage.createContentsMessage();
            mMessenger.send(message);
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
