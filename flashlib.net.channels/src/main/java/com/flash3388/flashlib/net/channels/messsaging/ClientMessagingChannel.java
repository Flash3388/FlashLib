package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.ConnectableNetChannel;
import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.util.Optional;

public class ClientMessagingChannel extends MessagingChannelBase {

    enum UpdateRequest {
        START_CONNECT,
        PING_CHECK
    }

    private final SocketAddress mRemoteAddress;
    private final ServerClock mClock;

    private final BasicPingContext mPingContext;

    private boolean mIsConnected;

    public ClientMessagingChannel(NetChannelOpener<? extends ConnectableNetChannel> channelOpener,
                                  ChannelUpdater channelUpdater,
                                  KnownMessageTypes messageTypes,
                                  ChannelId ourId,
                                  ServerClock clock,
                                  Logger logger,
                                  SocketAddress remoteAddress) {
        super(channelOpener, channelUpdater, messageTypes, ourId, clock, logger);
        mRemoteAddress = remoteAddress;
        mClock = clock;

        messageTypes.put(PingMessage.TYPE);

        mPingContext = new BasicPingContext(
                this,
                ourId,
                clock.baseClock(),
                logger,
                new PingContextListener(this, clock, logger),
                Time.seconds(5),
                3);

        mIsConnected = false;
    }

    public void enableKeepAlive() {
        verifyNotClosed();
        verifyNotStarted();

        mPingContext.enable();
    }

    @Override
    protected ChannelData implCreateChannelWrapper(NetChannel channel, UpdateRegistration registration) {
        return new ChannelData(channel, registration);
    }

    @Override
    protected void implDoOnChannelOpen(ChannelData channel) {
        mIsConnected = false;
        startConnection();
    }

    @Override
    protected Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, NetAddress sender, MessageHeader header, Message message) {
        if (message.getType().equals(PingMessage.TYPE)) {
            PingMessage pingMessage = (PingMessage) message;
            mPingContext.onPingResponse(header, pingMessage);
            return Optional.empty();
        }

        // fixed timestamp to client-only times
        Time sendTime = mClock.adjustToClientTime(header.getSendTime());
        MessageHeader newHeader = new MessageHeader(
                header.getContentSize(),
                header.getSender(),
                header.getMessageType(),
                sendTime,
                header.isOnlyForServer()
        );

        ReceivedMessage receivedMessage = new ReceivedMessage(newHeader, message);
        return Optional.of(receivedMessage);
    }

    @Override
    protected void implDoOnChannelConnectable() {
        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("Channel is closed but received new connect update");
            return;
        }

        try {
            ConnectableNetChannel connectableChannel = (ConnectableNetChannel) channel.channel;
            connectableChannel.finishConnection();

            onConnectionSuccessful();
        } catch (IOException e) {
            mLogger.error("Error while trying to connect, retrying", e);
            resetChannel();
        }
    }

    @Override
    protected void implDoOnChannelUpdate(Object param) {
        if (!(param instanceof UpdateRequest)) {
            return;
        }

        UpdateRequest request = (UpdateRequest) param;
        switch (request) {
            case START_CONNECT:
                startConnection();
                break;
            case PING_CHECK: {
                ChannelData channel = getChannel();
                if (channel == null) {
                    mLogger.warn("Channel is closed but received ping check update");
                    break;
                }

                if (!channel.readyForWriting) {
                    mLogger.warn("Channel is not connected but received ping check update");
                    break;
                }

                mPingContext.pingIfNecessary();
                channel.registration.requestUpdate(UpdateRequest.PING_CHECK);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void implDoOnChannelClose() {
        mPingContext.stop();

        if (mIsConnected) {
            reportToUserAsDisconnected();
            mIsConnected = false;
        }
    }

    private void startConnection() {
        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("start connection requested while channel is null");
            return;
        }

        mLogger.debug("Requesting channel connect to {}", mRemoteAddress);
        try {
            // TODO: SUPPORT CHANGING REMOTES
            ConnectableNetChannel connectableChannel = (ConnectableNetChannel) channel.channel;
            connectableChannel.startConnection(mRemoteAddress);

            channel.registration.requestConnectionUpdates();
        } catch (IOException | RuntimeException | Error e) {
            mLogger.error("Error while trying to start connection, retrying", e);
            retryConnection();
        }
    }

    private void retryConnection() {
        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("retry connection requested while channel is null");
            return;
        }

        mLogger.debug("Requesting retry start connect");
        channel.registration.requestUpdate(UpdateRequest.START_CONNECT);
    }

    private void onConnectionSuccessful() {
        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("connection successful while channel is null");
            return;
        }

        mLogger.debug("Channel connected");

        markReadyForWriting();

        if (mPingContext.isEnabled()) {
            mPingContext.start();
            channel.registration.requestUpdate(UpdateRequest.PING_CHECK);
        }

        mIsConnected = true;
        reportToUserAsConnected();
    }

    private static class PingContextListener implements BasicPingContext.Listener {

        private final WeakReference<MessagingChannelBase> mChannel;
        private final ServerClock mClock;
        private final Logger mLogger;

        private PingContextListener(MessagingChannelBase channel, ServerClock clock, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mClock = clock;
            mLogger = logger;
        }

        @Override
        public void onPingResponse(MessageHeader header, PingMessage message) {
            mClock.readjustOffset(header.getSendTime(), message.getTime());
        }

        @Override
        public void onPingNoResponse() {
            MessagingChannelBase channel = mChannel.get();
            if (channel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            channel.resetChannel();
        }
    }
}
