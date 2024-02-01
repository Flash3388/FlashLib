package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.channels.messsaging.BaseMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class HfcsContext {

    private final Clock mClock;
    private final Logger mLogger;

    private final EventController mEventController;
    private final KnownInDataTypes mInDataTypes;
    private final HfcsMessageType mMessageType;
    private final DelayQueue<OutDataNode> mOutDataQueue;
    private final Map<HfcsType, InDataNode> mInDataNodes;

    private final List<OutDataNode> mLocalSendQueue;
    private boolean mIsAttached;

    HfcsContext(Clock clock, Logger logger) {
        mClock = clock;
        mLogger = logger;

        mEventController = Controllers.newSyncExecutionController();
        mInDataTypes = new KnownInDataTypes();
        mMessageType = new HfcsMessageType(mInDataTypes, mLogger);
        mOutDataQueue = new DelayQueue<>();
        mInDataNodes = new ConcurrentHashMap<>();

        mLocalSendQueue = new ArrayList<>();
        mIsAttached = false;
    }

    public MessageType getMessageType() {
        return mMessageType;
    }

    public boolean isAttached() {
        return mIsAttached;
    }

    public void markedAttached() {
        resetNodes();
        mIsAttached = true;
    }

    public void markedUnattached() {
        // todo: report this to users
        mIsAttached = false;
    }

    public <T extends Serializable> HfcsRegisteredIncoming<T> updateNewIncoming(HfcsInType<T> type, Time receiveTimeout) {
        mInDataTypes.put(type);
        mInDataNodes.put(type, new InDataNode(type, receiveTimeout));
        return new RegisteredIncomingImpl<>(mEventController, type);
    }

    public void updateNewOutgoing(HfcsType type, Time period, Supplier<? extends Serializable> supplier) {
        mOutDataQueue.add(new OutDataNode(type, supplier, period));
    }

    public void updateReceivedNewData(MessageHeader header, HfcsUpdateMessage message) {
        InDataNode node = mInDataNodes.get(message.getHfcsType());
        if (node == null) {
            mLogger.debug("Received new data for unknown node: type={}", message.getHfcsType());
            return;
        }

        mLogger.debug("Received new data for node type={}", node.getType());

        Time now = mClock.currentTime();
        node.updateReceived(now);

        //noinspection unchecked,rawtypes
        mEventController.fire(
                new DataReceivedEvent(header.getSender().getInstanceId(), node.getType(), message.getData()),
                DataReceivedEvent.class,
                HfcsInListener.class,
                HfcsInListener::onReceived
        );
    }

    public void updateIncoming() {
        Time now = mClock.currentTime();
        for (InDataNode node : mInDataNodes.values()) {
            if (node.markTimedoutIfNecessary(now)) {
                mLogger.warn("Incoming data type={} has reached timeout", node.getType());

                //noinspection unchecked
                mEventController.fire(
                        new TimeoutEvent<>(node.getType()),
                        TimeoutEvent.class,
                        HfcsInListener.class,
                        HfcsInListener::onTimeout
                );
            }
        }
    }

    public void queueNewData(BaseMessagingChannel channel) {
        mLocalSendQueue.clear();
        mOutDataQueue.drainTo(mLocalSendQueue);

        if (mLocalSendQueue.isEmpty()) {
            return;
        }

        for (OutDataNode node : mLocalSendQueue) {
            try {
                mLogger.debug("Sending new data from type={}", node.getType());

                Serializable data = node.getNewData();
                channel.queue(new HfcsUpdateMessage(mMessageType, node.getType(), data));
            } finally {
                node.updateSent();
                mOutDataQueue.add(node);
            }
        }
    }

    public Time getWaitTimeToNextSend() {
        OutDataNode node = mOutDataQueue.peek();
        if (node == null) {
            return Time.INVALID;
        }

        return Time.milliseconds(node.getDelay(TimeUnit.MILLISECONDS));
    }

    private void resetNodes() {
        Time now = mClock.currentTime();

        for (InDataNode node : mInDataNodes.values()) {
            node.reset(now);
        }

        for (OutDataNode node : mOutDataQueue) {
            node.reset();
        }
    }
}
