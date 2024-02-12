package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.RemoteConfigurableChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Optional;

public class ReplyingNonConnectedMesssagingChannel extends MessagingChannelBase {

    private static final Time MAX_TIME_BETWEEN_MESSAGES = Time.seconds(1);

    private final Clock mClock;

    private boolean mIsConnected;
    private Time mLastReceiveTime;
    private NetAddress mAcknowledgedRemote;

    public ReplyingNonConnectedMesssagingChannel(NetChannelOpener<? extends RemoteConfigurableChannel> channelOpener,
                                                 ChannelUpdater channelUpdater,
                                                 KnownMessageTypes messageTypes,
                                                 ChannelId ourId,
                                                 Clock clock,
                                                 Logger logger) {
        super(channelOpener, channelUpdater, messageTypes, ourId, clock, logger);

        messageTypes.put(PingMessage.TYPE);

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
    protected Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, NetAddress sender, MessageHeader header, Message message) {
        mLastReceiveTime = mClock.currentTime();

        if (!mIsConnected) {
            mLogger.info("Received first message from {}, registering as our remote", sender);
            mIsConnected = true;

            RemoteConfigurableChannel actualChannel = (RemoteConfigurableChannel) channel.channel;
            actualChannel.setRemote(sender);

            mAcknowledgedRemote = sender;

            channel.registration.requestUpdate(null);
            reportToUserAsConnected();
        }
        if (!sender.equals(mAcknowledgedRemote)) {
            mLogger.warn("Received data not from acknowledged remote, ignoring");
            return Optional.empty();
        }

        if (PingMessage.TYPE.equals(message.getType())) {
            // we do want to account for pings sent by remote, and respond
            queue(message);
            return Optional.empty();
        }

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
