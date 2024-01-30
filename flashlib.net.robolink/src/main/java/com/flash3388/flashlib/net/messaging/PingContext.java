package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

class PingContext {

    private static final Time PING_INTERVAL = Time.milliseconds(500);
    private static final int MAX_UNRESPONDED_PINGS = 3;

    private final MessagingChannel mChannel;
    private final ServerClock mClock;
    private final Logger mLogger;

    private boolean mShouldPing;
    private Time mLastPingTime;
    private boolean mPingSent;
    private boolean mLastPingResponded;
    private int mUnrespondedPingCount;

    PingContext(MessagingChannel channel, ServerClock clock, Logger logger) {
        mChannel = channel;
        mClock = clock;
        mLogger = logger;

        mShouldPing = false;
        mLastPingTime = Time.INVALID;
        mUnrespondedPingCount = 0;
        mPingSent = false;
        mLastPingResponded = false;
    }

    public void onConnect() {
        mShouldPing = true;
        mLastPingTime = Time.INVALID;
        mUnrespondedPingCount = 0;
        mPingSent = false;
        mLastPingResponded = false;

        sendPing();
    }

    public void onDisconnect() {
        mShouldPing = false;
    }

    public void pingIfNecessary() {
        if (!mShouldPing) {
            return;
        }

        Time now = mClock.currentTimeUnmodified();
        if (!mLastPingTime.isValid() || now.sub(mLastPingTime).after(PING_INTERVAL)) {
            if (mPingSent && !mLastPingResponded) {
                mLogger.warn("PingContext: Last ping did not receive response");
                mUnrespondedPingCount++;
            }
            if (mUnrespondedPingCount >= MAX_UNRESPONDED_PINGS) {
                mLogger.warn("PingContext: Too many pings did not receive response, resetting");
                mChannel.resetConnection();
                mShouldPing = false;
                return;
            }

            sendPing();
        }
    }

    public void sendPing() {
        mLogger.debug("PingContext: Sending ping message");

        Time now = mClock.currentTimeUnmodified();

        mLastPingTime = now;
        mPingSent = true;
        mLastPingResponded = false;

        mChannel.queue(new PingMessage(now), true);
    }

    public void onPingResponse(MessageHeader header, PingMessage message) {
        mLogger.debug("PingContext: Received ping response");
        mLastPingResponded = true;
        mPingSent = false;
        mUnrespondedPingCount = 0;

        mClock.readjustOffset(header.getSendTime(), message.getTime());
    }
}
