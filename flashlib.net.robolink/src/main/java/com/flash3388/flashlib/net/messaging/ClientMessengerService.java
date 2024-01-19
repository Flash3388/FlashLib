package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.tcp.TcpClientConnector;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.SocketAddress;

public class ClientMessengerService extends MessengerServiceBase<MessagingChannel> {

    private final SocketAddress mServerAddress;
    private final KnownMessageTypes mMessageTypes;

    public ClientMessengerService(InstanceId instanceId,
                                  Clock clock,
                                  SocketAddress serverAddress,
                                  KnownMessageTypes messageTypes) {
        super(instanceId, clock);
        mServerAddress = serverAddress;
        mMessageTypes = messageTypes;
    }

    @Override
    protected MessagingChannel createChannel() {
        return new BasicMessagingChannelImpl(
                new TcpClientConnector(mClock, LOGGER),
                mServerAddress,
                mOurId,
                mClock,
                LOGGER,
                mMessageTypes);
    }

    @Override
    protected Runnable createReadTask() {
        return new ClientReadTask(mChannel, mEventController, mClock, LOGGER);
    }
}
