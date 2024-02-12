package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.Optional;

public class ReplyingNonConnectedMesssagingChannel extends MessagingChannelBase {

    private static final Time MAX_TIME_BETWEEN_MESSAGES = Time.seconds(1);

    private final Clock mClock;

    private boolean mIsConnected;
    private Time mLastReceiveTime;
    private SocketAddress mAcknowledgedRemote;

    public ReplyingNonConnectedMesssagingChannel(NetChannelOpener<? extends NetChannel> channelOpener,
                                                 ChannelUpdater channelUpdater,
                                                 KnownMessageTypes messageTypes,
                                                 ChannelId ourId,
                                                 Clock clock,
                                                 Logger logger) {
        super(channelOpener, channelUpdater, messageTypes, ourId, clock, logger);

        mClock = clock;
        mIsConnected = false;
        mLastReceiveTime = Time.INVALID;
        mAcknowledgedRemote = null;
    }

    @Override
    protected ChannelData implCreateChannelWrapper(NetChannel channel, UpdateRegistration registration) {
        return new ChannelData(channel, registration);
    }

    @Override
    protected void implDoOnChannelOpen(ChannelData channel) {
        mIsConnected = false;
        markReadyForWriting();
    }

    @Override
    protected Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, MessageHeader header, Message message) {
        mLastReceiveTime = mClock.currentTime();

        if (!mIsConnected) {
            mIsConnected = true;

            // TODO: GET REMOTE
            // TODO: ASSIGN REMOTE TO CHANNEL (use a specific channel connector and cast channel into wanted one)
            mAcknowledgedRemote = null;

            channel.registration.requestUpdate(null);
            reportToUserAsConnected();
        }

        // TODO: CHECK THIS IS FROM THE ACKNOWLEDGED REMOTE

        ReceivedMessage receivedMessage = new ReceivedMessage(header, message);
        return Optional.of(receivedMessage);
    }

    @Override
    protected void implDoOnChannelConnectable() {

    }

    @Override
    protected void implDoOnChannelUpdate(Object param) {
        if (!mIsConnected) {
            mLogger.warn("Channel is not connected but update called");
            return;
        }

        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("Channel is closed but update called");
            return;
        }

        Time now = mClock.currentTime();
        if (now.sub(mLastReceiveTime).largerThanOrEquals(MAX_TIME_BETWEEN_MESSAGES)) {
            mLogger.warn("Data was not received for too long");
            markNotReadyForWriting();
            mIsConnected = false;
            mAcknowledgedRemote = null;
            mLastReceiveTime = Time.INVALID;
            reportToUserAsDisconnected();
        } else {
            channel.registration.requestUpdate(null);
        }
    }

    @Override
    protected void implDoOnChannelClose() {

    }
}
