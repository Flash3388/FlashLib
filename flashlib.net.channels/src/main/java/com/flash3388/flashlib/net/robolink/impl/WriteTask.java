package com.flash3388.flashlib.net.robolink.impl;

import com.flash3388.flashlib.net.robolink.io.RoboLinkChannel;
import com.flash3388.flashlib.net.robolink.io.UnknownRemoteException;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class WriteTask implements Runnable {

    private static final Time MINIMUM_LOOP_TIME = Time.milliseconds(50);

    private final RoboLinkChannel mChannel;
    private final BlockingQueue<SendRequest> mRequestsQueue;
    private final Clock mClock;
    private final Logger mLogger;

    private final AtomicReference<Time> mLastSend;

    public WriteTask(RoboLinkChannel channel, BlockingQueue<SendRequest> requestsQueue, Clock clock, Logger logger) {
        mChannel = channel;
        mRequestsQueue = requestsQueue;
        mClock = clock;
        mLogger = logger;

        mLastSend = new AtomicReference<>(null);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // if the queue has so little that it will cause a continuous loop, we should sleep
                Time now = mClock.currentTime();
                Time lastSend = mLastSend.getAndSet(now);
                if (now.sub(lastSend).lessThanOrEquals(MINIMUM_LOOP_TIME)) {
                    //noinspection BusyWait
                    Thread.sleep(MINIMUM_LOOP_TIME.valueAsMillis());
                }

                mLogger.debug("WriteTask waiting for packets to send");
                SendRequest request = mRequestsQueue.take();

                if (request.isBroadcast()) {
                    mChannel.broadcast(
                            request.getPacket().getContentType(),
                            request.getPacket().getContent()
                    );
                } else {
                    try {
                        mChannel.send(
                                request.getTargetId(),
                                request.getPacket().getContentType(),
                                request.getPacket().getContent()
                        );
                    } catch (UnknownRemoteException e) {
                        mRequestsQueue.add(request);
                    }
                }
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
                mLogger.error("Error in WriteTask", t);
            }
        }
    }
}
