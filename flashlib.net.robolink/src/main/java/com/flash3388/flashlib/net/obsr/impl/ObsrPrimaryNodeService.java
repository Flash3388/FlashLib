package com.flash3388.flashlib.net.obsr.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.TcpServerMessagingChannel;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import com.flash3388.flashlib.net.obsr.Storage;
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

public class ObsrPrimaryNodeService extends ObsrNodeServiceBase {

    private static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    private final TcpServerMessagingChannel mChannel;

    public ObsrPrimaryNodeService(InstanceId ourId, Clock clock, SocketAddress bindAddress) {
        super(ourId, clock);

        KnownMessageTypes messageTypes = getMessageTypes();
        mChannel = new TcpServerMessagingChannel(bindAddress, ourId, LOGGER, messageTypes);
    }

    public ObsrPrimaryNodeService(InstanceId ourId, Clock clock, String bindAddress) {
        this(ourId, clock, new InetSocketAddress(bindAddress, Constants.PRIMARY_NODE_PORT));
    }

    public ObsrPrimaryNodeService(InstanceId ourId, Clock clock) {
        this(ourId, clock, "0.0.0.0");
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("ObsrPrimaryNodeService-UpdateTask",
                new UpdateTask(mChannel, mStorage, LOGGER, mWriteQueue));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }

    private static class UpdateTask extends UpdateTaskBase {

        private final TcpServerMessagingChannel mChannel;
        private final Logger mLogger;
        private final TcpServerMessagingChannel.UpdateHandler mHandler;

        private UpdateTask(TcpServerMessagingChannel channel,
                           Storage storage,
                           Logger logger,
                           BlockingQueue<PendingWriteMessage> writeQueue) {
            super(logger, channel, writeQueue);

            mChannel = channel;
            mLogger = logger;
            mHandler = new ChannelUpdateHandler(storage, logger, writeQueue);
        }

        @Override
        protected void processUpdates() {
            mLogger.trace("Processing channel updates");

            try {
                mChannel.processUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error processing changes", e);
            }
        }
    }
}
