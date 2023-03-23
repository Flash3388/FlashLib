package com.flash3388.flashlib.net.obsr.impl;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.ServerMessagingChannel;
import com.flash3388.flashlib.net.message.TcpServerMessagingChannel;
import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ObsrPrimaryNodeService extends SingleUseService implements ObjectStorage {

    private final Logger mLogger;
    private final ServerMessagingChannel mChannel;
    private final Storage mStorage;

    private Thread mReadThread;

    public ObsrPrimaryNodeService(InstanceId ourId, SocketAddress bindAddress, Clock clock, Logger logger) {
        mLogger = logger;

        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(NewEntryMessage.TYPE);
        messageTypes.put(EntryClearMessage.TYPE);
        messageTypes.put(EntryChangeMessage.TYPE);
        messageTypes.put(StorageContentsMessage.TYPE);

        MessageWriter messageWriter = new MessageWriterImpl(ourId);
        MessageReader messageReader = new MessageReaderImpl(ourId, messageTypes);
        mChannel = new TcpServerMessagingChannel(bindAddress, messageWriter, messageReader, clock, logger);

        StorageListener listener = new StorageListenerImpl(mChannel, logger);
        mStorage = new StorageImpl(listener, clock);

        mReadThread = null;
    }

    public ObsrPrimaryNodeService(InstanceId ourId, Clock clock, Logger logger) {
        this(ourId, new InetSocketAddress("0.0.0.0", Constants.PRIMARY_NODE_PORT), clock, logger);
    }

    @Override
    public StoredObject getRoot() {
        return mStorage.getObject(StoragePath.root());
    }

    @Override
    protected void startRunning() throws ServiceException {
        mReadThread = new Thread(
                new ReadTask(mChannel, mStorage, mLogger),
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

        private final ServerMessagingChannel mChannel;
        private final Logger mLogger;
        private final ServerMessagingChannel.UpdateHandler mHandler;

        private ReadTask(ServerMessagingChannel channel, Storage storage, Logger logger) {
            mChannel = channel;
            mLogger = logger;
            mHandler = new ServerChannelUpdateHandler(storage, logger, channel);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.handleUpdates(mHandler);
                } catch (IOException e) {
                    mLogger.debug("Error processing changes", e);
                } catch (InterruptedException e) {
                    break;
                } catch (TimeoutException e) {
                    // oh, well
                }
            }
        }
    }

    private static class StorageListenerImpl implements StorageListener {

        private final WritableMessagingChannel mChannel;
        private final Logger mLogger;

        public StorageListenerImpl(WritableMessagingChannel channel, Logger logger) {
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void onNewEntry(StoragePath path) {
            try {
                mChannel.write(NewEntryMessage.TYPE, new NewEntryMessage(path.toString()));
            } catch (IOException e) {
                mLogger.debug("error writing message from storage", e);
            } catch (InterruptedException e) {
                // we don't care about this
            }
        }

        @Override
        public void onEntryUpdate(StoragePath path, Value value) {
            try {
                mChannel.write(EntryChangeMessage.TYPE, new EntryChangeMessage(path.toString(), value));
            } catch (IOException e) {
                mLogger.debug("error writing message from storage", e);
            } catch (InterruptedException e) {
                // we don't care about this
            }
        }

        @Override
        public void onEntryClear(StoragePath path) {
            try {
                mChannel.write(EntryClearMessage.TYPE, new EntryClearMessage(path.toString()));
            } catch (IOException e) {
                mLogger.debug("error writing message from storage", e);
            } catch (InterruptedException e) {
                // we don't care about this
            }
        }
    }
}