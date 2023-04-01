package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.TimeoutEvent;
import com.flash3388.flashlib.net.hfcs.TimeoutListener;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;

public class BasicUpdateTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Clock mClock;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;
    private final Collection<InDataNode> mInDataNodes;
    private final EventController mEventController;

    public BasicUpdateTask(MessagingChannel channel,
                           Clock clock,
                           Logger logger,
                           MessagingChannel.UpdateHandler handler,
                           Collection<InDataNode> inDataNodes,
                           EventController eventController) {
        mChannel = channel;
        mClock = clock;
        mLogger = logger;
        mHandler = handler;
        mInDataNodes = inDataNodes;
        mEventController = eventController;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mChannel.processUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error in processing updates", e);
            }

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
    }
}
