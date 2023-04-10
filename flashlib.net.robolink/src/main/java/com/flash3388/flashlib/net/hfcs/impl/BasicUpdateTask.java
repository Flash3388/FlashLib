package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.TimeoutEvent;
import com.flash3388.flashlib.net.hfcs.TimeoutListener;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.HfcsOutMessage;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class BasicUpdateTask implements Runnable {

    private static final long SLEEP_TIME_MS = 5;

    private final MessagingChannel mChannel;
    private final Clock mClock;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;
    private final Collection<InDataNode> mInDataNodes;
    private final EventController mEventController;
    private final BlockingQueue<OutDataNode> mDataQueue;
    private final MessageType mMessageType;

    public BasicUpdateTask(MessagingChannel channel,
                           Clock clock,
                           Logger logger,
                           MessagingChannel.UpdateHandler handler,
                           Collection<InDataNode> inDataNodes,
                           EventController eventController,
                           BlockingQueue<OutDataNode> dataQueue,
                           MessageType messageType) {
        mChannel = channel;
        mClock = clock;
        mLogger = logger;
        mHandler = handler;
        mInDataNodes = inDataNodes;
        mEventController = eventController;
        mDataQueue = dataQueue;
        mMessageType = messageType;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                processUpdates();
                updateInNodes();
                processOutData();

                //noinspection BusyWait
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void processUpdates() {
        mLogger.trace("Processing channel updates");

        try {
            mChannel.processUpdates(mHandler);
        } catch (IOException e) {
            mLogger.error("Error in processing updates", e);
        }
    }

    private void updateInNodes() {
        mLogger.trace("Updating in nodes");

        Time now = mClock.currentTime();
        for (InDataNode node : mInDataNodes) {
            if (node.hasReceiveTimedOut(now)) {
                node.markTimedOut();

                //noinspection unchecked
                mEventController.fire(
                        new TimeoutEvent<>(node.getType()),
                        TimeoutEvent.class,
                        TimeoutListener.class,
                        TimeoutListener::onTimeout
                );
            }
        }
    }

    private void processOutData() throws InterruptedException {
        mLogger.trace("Processing out queue");

        try {
            int size = mDataQueue.size();
            while (size-- > 0) {
                OutDataNode node = mDataQueue.poll();
                if (node == null) {
                    break;
                }

                Type type = node.getType();
                OutData data = node.getData();
                node.updateSent();
                mDataQueue.add(node);

                mLogger.debug("Sending data of type {}", type.getKey());
                mChannel.write(mMessageType, new HfcsOutMessage(type, data));
            }
        } catch (IOException e) {
            mLogger.error("Error while sending data", e);
        }
    }
}
