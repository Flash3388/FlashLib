package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Optional;

public class NonConnectedMessagingChannel extends MessagingChannelBase {

    public NonConnectedMessagingChannel(NetChannelOpener<? extends NetChannel> channelOpener,
                                        ChannelUpdater channelUpdater,
                                        KnownMessageTypes messageTypes,
                                        ChannelId ourId,
                                        Clock clock,
                                        Logger logger) {
        super(channelOpener, channelUpdater, messageTypes, ourId, clock, logger);
    }

    @Override
    protected ChannelData implCreateChannelWrapper(NetChannel channel, UpdateRegistration registration) {
        return new ChannelData(channel, registration);
    }

    @Override
    protected void implDoOnChannelOpen(ChannelData channel) {
        markReadyForWriting();
        reportToUserAsConnected();
    }

    @Override
    protected Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, MessageHeader header, Message message) {
        ReceivedMessage receivedMessage = new ReceivedMessage(header, message);
        return Optional.of(receivedMessage);
    }

    @Override
    protected void implDoOnChannelConnectable() {

    }

    @Override
    protected void implDoOnChannelUpdate(Object param) {

    }

    @Override
    protected void implDoOnChannelClose() {

    }
}
