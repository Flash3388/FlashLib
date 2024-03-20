package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class BasicPingContext {

    interface Listener {
        void onPingResponse(MessageHeader header, PingMessage message);
        void onPingNoResponse();
    }

    private final WeakReference<MessagingChannelBase> mChannel;
    private final ChannelId mOurId;
    private final Clock mClock;
    private final Logger mLogger;
    private final Listener mListener;
    private final Time mPingInterval;
    private final int mMaxRespondedPings;

    private boolean mEnabled;
    private boolean mShouldPing;
    private Time mLastPingTime;
    private boolean mPingSent;
    private boolean mLastPingResponded;
    private int mUnrespondedPingCount;

    BasicPingContext(MessagingChannelBase channel, ChannelId ourId, Clock clock, Logger logger, Listener listener, Time pingInterval, int maxRespondedPings) {
        mChannel = new WeakReference<>(channel);
        mOurId = ourId;
        mClock = clock;
        mLogger = logger;
        mListener = listener;
        mPingInterval = pingInterval;
        mMaxRespondedPings = maxRespondedPings;

        mEnabled = false;
        mShouldPing = false;
        mLastPingTime = Time.INVALID;
        mUnrespondedPingCount = 0;
        mPingSent = false;
        mLastPingResponded = false;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void enable() {
        mEnabled = true;
    }

    public void start() {
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

    public void stop() {
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

        Time now = mClock.currentTime();
        if (!mLastPingTime.isValid() || now.sub(mLastPingTime).after(mPingInterval)) {
            if (mPingSent && !mLastPingResponded) {
                mLogger.warn("PingContext: Last ping did not receive response");
                mUnrespondedPingCount++;
            }
            if (mUnrespondedPingCount >= mMaxRespondedPings) {
                mLogger.warn("PingContext: Too many pings did not receive response");
                mShouldPing = false;

                mListener.onPingNoResponse();

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

        Time now = mClock.currentTime();

        mLastPingTime = now;
        mPingSent = true;
        mLastPingResponded = false;

        MessagingChannelBase channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("MessagingChannel was garbage collected");
            return;
        }

        channel.queue(new PingMessage(mOurId, now), true);
    }

    public void onPingResponse(MessageHeader header, PingMessage message) {
        if (!mEnabled) {
            return;
        }

        mLogger.debug("PingContext: Received ping response");
        mLastPingResponded = true;
        mPingSent = false;
        mUnrespondedPingCount = 0;

        mListener.onPingResponse(header, message);
    }
}
