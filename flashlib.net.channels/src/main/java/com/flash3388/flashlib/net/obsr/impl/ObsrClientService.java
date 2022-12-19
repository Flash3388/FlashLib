package com.flash3388.flashlib.net.obsr.impl;

import com.castle.concurrent.service.SingleUseService;
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
import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;

public class ObsrClientService extends SingleUseService implements ObjectStorage {

    private final Logger mLogger;
    private final MessagingChannel mChannel;
    private final Storage mStorage;

    private Thread mReadThread;

    public ObsrClientService(InstanceId ourId, SocketAddress serverAddress, Clock clock, Logger logger) {
        mLogger = logger;

        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(NewEntryMessage.TYPE);
        messageTypes.put(EntryClearMessage.TYPE);
        messageTypes.put(EntryChangeMessage.TYPE);
        messageTypes.put(StorageContentsMessage.TYPE);

        MessageWriter messageWriter = new MessageWriterImpl(ourId);
        MessageReader messageReader = new MessageReaderImpl(messageTypes);
        mChannel = new TcpClientMessagingChannel(serverAddress, messageWriter, messageReader, clock, logger);

        StorageListener listener = new StorageListenerImpl(mChannel, logger);
        mStorage = new StorageImpl(listener, clock, logger);

        mReadThread = null;
    }

    @Override
    public StoredObject getChild(String name) {
        return mStorage.getObject(StoragePath.create(name));
    }

    @Override
    public StoredEntry getEntry(String name) {
        return mStorage.getEntry(StoragePath.create(name));
    }

    @Override
    protected void startRunning() throws ServiceException {
        mReadThread = new Thread(
                new ReadTask(mChannel, mStorage, mLogger),
                "ObsrClientService-ReadTask");
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

        private final MessagingChannel mChannel;
        private final Logger mLogger;
        private final MessagingChannel.UpdateHandler mHandler;

        private ReadTask(MessagingChannel channel, Storage storage, Logger logger) {
            mChannel = channel;
            mLogger = logger;
            mHandler = new ChannelUpdateHandler(storage, logger);
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
}
