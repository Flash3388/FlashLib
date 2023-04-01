package com.flash3388.flashlib.net.obsr.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannel;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.tcp.TcpClientConnector;
import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.messages.RequestContentMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ObsrSecondaryNodeService extends ObsrNodeServiceBase implements ObjectStorage {

    private static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    private final MessagingChannel mChannel;

    public ObsrSecondaryNodeService(InstanceId ourId, Clock clock, SocketAddress serverAddress) {
        super(ourId, clock);

        KnownMessageTypes messageTypes = getMessageTypes();
        mChannel = new BasicMessagingChannel(
                new TcpClientConnector(LOGGER),
                serverAddress,
                ourId,
                LOGGER,
                messageTypes
        );
    }

    public ObsrSecondaryNodeService(InstanceId ourId, Clock clock, String serverAddress) {
        this(ourId, clock, new InetSocketAddress(serverAddress, Constants.PRIMARY_NODE_PORT));
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("ObsrSecondaryNodeService-UpdateTask",
                new UpdateTask(mChannel, mStorage, LOGGER, mWriteQueue));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }

    private static class UpdateTask extends UpdateTaskBase {

        private final MessagingChannel mChannel;
        private final Logger mLogger;
        private final MessagingChannel.UpdateHandler mHandler;
        private final BlockingQueue<PendingWriteMessage> mQueue;

        private UpdateTask(MessagingChannel channel,
                           Storage storage,
                           Logger logger,
                           BlockingQueue<PendingWriteMessage> queue) {
            super(logger, channel, queue);
            mChannel = channel;
            mLogger = logger;
            mQueue = queue;
            mHandler = new ChannelUpdateHandler(storage, logger, queue);
        }

        @Override
        protected void processUpdates() {
            mLogger.trace("Processing channel updates");

            try {
                mChannel.processUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error processing changes", e);

                mLogger.debug("Requesting full storage content");
                mQueue.add(new PendingWriteMessage(RequestContentMessage.TYPE, new RequestContentMessage()));
            }
        }
    }
}
