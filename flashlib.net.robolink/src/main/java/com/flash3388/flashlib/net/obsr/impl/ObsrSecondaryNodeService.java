package com.flash3388.flashlib.net.obsr.impl;

import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.net.message.TcpClientMessagingChannel;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryDeleteMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.RequestContentMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObsrSecondaryNodeService extends ObsrNodeServiceBase implements ObjectStorage {

    private static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    private final MessagingChannel mChannel;
    private final Storage mStorage;
    private final BlockingQueue<PendingWriteMessage> mWriteQueue;

    private Thread mReadThread;
    private Thread mWriteThread;

    public ObsrSecondaryNodeService(InstanceId ourId, SocketAddress serverAddress, Clock clock) {
        super(ourId);

        KnownMessageTypes messageTypes = getMessageTypes();
        MessageWriter messageWriter = new MessageWriterImpl(ourId);
        MessageReader messageReader = new MessageReaderImpl(ourId, messageTypes);
        mChannel = new TcpClientMessagingChannel(serverAddress, messageWriter, messageReader, clock, LOGGER);

        mWriteQueue = new LinkedBlockingQueue<>();

        StorageListener listener = new StorageListenerImpl(mWriteQueue, LOGGER);
        mStorage = new StorageImpl(listener, clock);

        mReadThread = null;
        mWriteThread = null;
    }

    public ObsrSecondaryNodeService(InstanceId ourId, String serverAddress, Clock clock) {
        this(ourId, new InetSocketAddress(serverAddress, Constants.PRIMARY_NODE_PORT), clock);
    }

    @Override
    protected Storage getStorage() {
        return mStorage;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mReadThread = new Thread(
                new ReadTask(mChannel, mStorage, LOGGER, mWriteQueue),
                "ObsrSecondaryNodeService-ReadTask");
        mReadThread.setDaemon(true);
        mReadThread.start();

        mWriteThread = new Thread(
                new WriteTask(mWriteQueue, mChannel, LOGGER),
                "ObsrSecondaryNodeService-WriteTask");
        mWriteThread.setDaemon(true);
        mWriteThread.start();
    }

    @Override
    protected void stopRunning() {
        mReadThread.interrupt();
        mReadThread = null;

        mWriteThread.interrupt();
        mWriteThread = null;

        Closeables.silentClose(mChannel);
    }

    private static class ReadTask implements Runnable {

        private final MessagingChannel mChannel;
        private final Logger mLogger;
        private final MessagingChannel.UpdateHandler mHandler;
        private final BlockingQueue<PendingWriteMessage> mQueue;

        private ReadTask(MessagingChannel channel, Storage storage, Logger logger,
                         BlockingQueue<PendingWriteMessage> queue) {
            mChannel = channel;
            mLogger = logger;
            mQueue = queue;
            mHandler = new ChannelUpdateHandler(storage, logger,
                    (type, msg)-> queue.add(new PendingWriteMessage(type, msg)));
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.handleUpdates(mHandler);
                } catch (IOException e) {
                    mLogger.error("Error processing changes", e);

                    mLogger.debug("Requesting full storage content");
                    mQueue.add(new PendingWriteMessage(RequestContentMessage.TYPE, new RequestContentMessage()));
                } catch (InterruptedException e) {
                    break;
                } catch (TimeoutException e) {
                    // oh, well
                }
            }
        }
    }

    private static class StorageListenerImpl implements StorageListener {

        private final BlockingQueue<PendingWriteMessage> mQueue;
        private final Logger mLogger;

        public StorageListenerImpl(BlockingQueue<PendingWriteMessage> queue, Logger logger) {
            mQueue = queue;
            mLogger = logger;
        }

        @Override
        public void onNewEntry(StoragePath path) {
            mLogger.debug("New entry in path {}", path);
            mQueue.add(new PendingWriteMessage(NewEntryMessage.TYPE, new NewEntryMessage(path.toString())));
        }

        @Override
        public void onEntryUpdate(StoragePath path, Value value) {
            mLogger.debug("Update to entry in path {}, value={}", path, value);
            mQueue.add(new PendingWriteMessage(EntryChangeMessage.TYPE, new EntryChangeMessage(path.toString(), value)));
        }

        @Override
        public void onEntryClear(StoragePath path) {
            mLogger.debug("Entry in path {} cleared", path);
            mQueue.add(new PendingWriteMessage(EntryClearMessage.TYPE, new EntryClearMessage(path.toString())));
        }

        @Override
        public void onEntryDeleted(StoragePath path) {
            mLogger.debug("Entry in path {} deleted", path);
            mQueue.add(new PendingWriteMessage(EntryDeleteMessage.TYPE, new EntryDeleteMessage(path.toString())));
        }
    }
}
