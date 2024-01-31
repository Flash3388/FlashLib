package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class ClientPingContext {

    private static final Time PING_INTERVAL = Time.milliseconds(1500);
    private static final int MAX_UNRESPONDED_PINGS = 3;

    private final WeakReference<MessagingChannelBase> mChannel;
    private final ServerClock mClock;
    private final Logger mLogger;

    private boolean mEnabled;
    private boolean mShouldPing;
    private Time mLastPingTime;
    private boolean mPingSent;
    private boolean mLastPingResponded;
    private int mUnrespondedPingCount;

    ClientPingContext(MessagingChannelBase channel, ServerClock clock, Logger logger) {
        mChannel = new WeakReference<>(channel);
        mClock = clock;
        mLogger = logger;

        mEnabled = false;
        mShouldPing = false;
        mLastPingTime = Time.INVALID;
        mUnrespondedPingCount = 0;
        mPingSent = false;
        mLastPingResponded = false;
    }

    public void enable() {
        mEnabled = true;
    }

    public void onConnect() {
        if (!mEnabled) {
            return;
        }

        mShouldPing = true;
        mLastPingTime = Time.INVALID;
        mUnrespondedPingCount = 0;
        mPingSent = false;
        mLastPingResponded = false;

        sendPing();
    }

    public void onDisconnect() {
        if (!mEnabled) {
            return;
        }

        mShouldPing = false;
    }

    public void pingIfNecessary() {
        if (!mEnabled || !mShouldPing) {
            return;
        }

        MessagingChannel channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("MessagingChannel was garbage collected");
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
                channel.resetChannel();
                mShouldPing = false;
                return;
            }

            sendPing();
        }
    }

    public void sendPing() {
        if (!mEnabled) {
            return;
        }

        mLogger.debug("PingContext: Sending ping message");

        Time now = mClock.currentTimeUnmodified();

        mLastPingTime = now;
        mPingSent = true;
        mLastPingResponded = false;

        MessagingChannelBase channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("MessagingChannel was garbage collected");
            return;
        }

        channel.queue(new PingMessage(now), true);
    }

    public void onPingResponse(MessageHeader header, PingMessage message) {
        if (!mEnabled) {
            return;
        }

        mLogger.debug("PingContext: Received ping response");
        mLastPingResponded = true;
        mPingSent = false;
        mUnrespondedPingCount = 0;

        mClock.readjustOffset(header.getSendTime(), message.getTime());
    }
}
