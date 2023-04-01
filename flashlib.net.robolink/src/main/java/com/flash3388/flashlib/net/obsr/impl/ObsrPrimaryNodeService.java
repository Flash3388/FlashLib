package com.flash3388.flashlib.net.obsr.impl;

import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.TcpServerMessagingChannel;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryDeleteMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ObsrPrimaryNodeService extends ObsrNodeServiceBase implements ObjectStorage {

    private static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    private final TcpServerMessagingChannel mChannel;
    private final Storage mStorage;

    private Thread mReadThread;

    public ObsrPrimaryNodeService(InstanceId ourId, SocketAddress bindAddress, Clock clock) {
        super(ourId);

        KnownMessageTypes messageTypes = getMessageTypes();
        mChannel = new TcpServerMessagingChannel(bindAddress, ourId, LOGGER, messageTypes);

        StorageListener listener = new StorageListenerImpl(mChannel, LOGGER);
        mStorage = new StorageImpl(listener, clock);

        mReadThread = null;
    }

    public ObsrPrimaryNodeService(InstanceId ourId, String bindAddress, Clock clock) {
        this(ourId, new InetSocketAddress(bindAddress, Constants.PRIMARY_NODE_PORT), clock);
    }

    public ObsrPrimaryNodeService(InstanceId ourId, Clock clock) {
        this(ourId, "0.0.0.0", clock);
    }

    @Override
    protected Storage getStorage() {
        return mStorage;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mReadThread = new Thread(
                new ReadTask(mChannel, mStorage, LOGGER),
                "ObsrPrimaryNodeService-ReadTask");
        mReadThread.setDaemon(true);
        mReadThread.start();
    }

    @Override
    protected void stopRunning() {
        mReadThread.interrupt();
        mReadThread = null;

        Closeables.silentClose(mChannel);
    }

    private static class ReadTask implements Runnable {

        private final TcpServerMessagingChannel mChannel;
        private final Logger mLogger;
        private final TcpServerMessagingChannel.UpdateHandler mHandler;

        private ReadTask(TcpServerMessagingChannel channel, Storage storage, Logger logger) {
            mChannel = channel;
            mLogger = logger;
            mHandler = new ChannelUpdateHandler(storage, logger, (type, message)-> {
                try {
                    channel.write(type, message);
                } catch (IOException e) {
                    logger.error("Error transferring message to client", e);
                }
            });
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.processUpdates(mHandler);
                } catch (IOException e) {
                    mLogger.error("Error processing changes", e);
                }
            }
        }
    }

    private static class StorageListenerImpl implements StorageListener {

        private final MessagingChannel mChannel;
        private final Logger mLogger;

        public StorageListenerImpl(MessagingChannel channel, Logger logger) {
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void onNewEntry(StoragePath path) {
            try {
                mLogger.debug("New entry in path {}", path);
                mChannel.write(NewEntryMessage.TYPE, new NewEntryMessage(path.toString()));
            } catch (IOException e) {
                mLogger.error("error writing message from storage", e);
            }
        }

        @Override
        public void onEntryUpdate(StoragePath path, Value value) {
            try {
                mLogger.debug("Update to entry in path {}, value={}", path, value);
                mChannel.write(EntryChangeMessage.TYPE, new EntryChangeMessage(path.toString(), value));
            } catch (IOException e) {
                mLogger.error("error writing message from storage", e);
            }
        }

        @Override
        public void onEntryClear(StoragePath path) {
            try {
                mLogger.debug("Entry in path {} cleared", path);
                mChannel.write(EntryClearMessage.TYPE, new EntryClearMessage(path.toString()));
            } catch (IOException e) {
                mLogger.error("error writing message from storage", e);
            }
        }

        @Override
        public void onEntryDeleted(StoragePath path) {
            try {
                mLogger.debug("Entry in path {} deleted", path);
                mChannel.write(EntryDeleteMessage.TYPE, new EntryDeleteMessage(path.toString()));
            } catch (IOException e) {
                mLogger.error("error writing message from storage", e);
            }
        }
    }
}
