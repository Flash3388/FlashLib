package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.tcp.TcpServerChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.SocketAddress;
import java.util.function.Consumer;

public class ServerMessagingService extends MessengerServiceBase<ServerMessagingChannel> {

    private final SocketAddress mBindAddress;
    private final KnownMessageTypes mMessageTypes;
    private final Consumer<InstanceId> mOnClientConnection;

    public ServerMessagingService(InstanceId instanceId,
                                  Clock clock,
                                  SocketAddress bindAddress,
                                  KnownMessageTypes messageTypes,
                                  Consumer<InstanceId> onClientConnection) {
        super(instanceId, clock);
        mBindAddress = bindAddress;
        mMessageTypes = messageTypes;
        mOnClientConnection = onClientConnection;
    }

    @Override
    protected ServerMessagingChannel createChannel() {
        return new ServerMessagingChannelImpl(
                new TcpServerChannel(mBindAddress, LOGGER),
                mOurId,
                mMessageTypes,
                mClock,
                LOGGER
        );
    }

    @Override
    protected Runnable createReadTask() {
        return new ServerReadTask(mChannel, mEventController, mClock, LOGGER, mOnClientConnection);
    }
}
