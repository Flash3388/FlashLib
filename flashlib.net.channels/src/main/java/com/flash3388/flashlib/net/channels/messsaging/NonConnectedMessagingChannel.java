package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class NonConnectedMessagingChannel extends MessagingChannelBase {

    private static final Time PING_TIMEOUT = Time.seconds(1);
    private static final int PING_RESPONDED_PINGS = 2;

    private final BasicPingContext mPingContext;
    private boolean mIsConnected;

    public NonConnectedMessagingChannel(NetChannelOpener<? extends NetChannel> channelOpener,
                                        ChannelUpdater channelUpdater,
                                        KnownMessageTypes messageTypes,
                                        ChannelId ourId,
                                        Clock clock,
                                        Logger logger) {
        super(channelOpener, channelUpdater, messageTypes, ourId, clock, logger);

        messageTypes.put(PingMessage.TYPE);

        mPingContext = new BasicPingContext(
                this,
                ourId,
                clock,
                logger,
                new PingContextListener(this, logger),
                PING_TIMEOUT,
                PING_RESPONDED_PINGS
        );

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
        markReadyForWriting();

        if (mPingContext.isEnabled()) {
            mPingContext.start();
            channel.registration.requestUpdate(null);
        } else {
            mIsConnected = true;
            reportToUserAsConnected();
        }
    }

    @Override
    protected Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, NetAddress sender, MessageHeader header, Message message) {
        if (message.getType().equals(PingMessage.TYPE)) {
            PingMessage pingMessage = (PingMessage) message;
            if (pingMessage.getOriginalSender().equals(getOurId())) {
                mPingContext.onPingResponse(header, pingMessage);
            } else {
                // not our ping, but indicates we are still connected
                mPingContext.onPingResponse(header, pingMessage);

                // respond to their ping
                queue(pingMessage);
            }

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
        ChannelData channel = getChannel();
        if (channel == null) {
            mLogger.warn("Channel is closed but received ping check update");
            return;
        }

        if (!channel.readyForWriting) {
            mLogger.warn("Channel is not ready but received ping check update");
            return;
        }

        mPingContext.pingIfNecessary();
        channel.registration.requestUpdate(null);
    }

    @Override
    protected void implDoOnChannelClose() {
        mPingContext.stop();

        if (mIsConnected) {
            reportToUserAsDisconnected();
            mIsConnected = false;
        }
    }

    private void onPingResponse() {
        if (!mIsConnected) {
            mIsConnected = true;
            reportToUserAsConnected();
        }
    }

    private void onPingNoResponse() {
        if (mIsConnected) {
            mIsConnected = false;
            reportToUserAsDisconnected();
        }

        // restart the pinger to try pinging still
        mPingContext.start();
    }

    private static class PingContextListener implements BasicPingContext.Listener {

        private final WeakReference<NonConnectedMessagingChannel> mChannel;
        private final Logger mLogger;

        private PingContextListener(NonConnectedMessagingChannel channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public void onPingResponse(MessageHeader header, PingMessage message) {
            NonConnectedMessagingChannel channel = mChannel.get();
            if (channel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            channel.onPingResponse();
        }

        @Override
        public void onPingNoResponse() {
            NonConnectedMessagingChannel channel = mChannel.get();
            if (channel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            channel.onPingNoResponse();
        }
    }
}
