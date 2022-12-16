package com.flash3388.flashlib.robot.net.impl;

import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.net.messaging.MessageHandler;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.impl.MessageService;
import com.flash3388.flashlib.net.messaging.io.ClientMessagingChannel;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.flash3388.flashlib.net.messaging.io.ServerConnectionService;
import com.flash3388.flashlib.net.messaging.io.ServerMessagingChannel;
import com.flash3388.flashlib.robot.net.MessagingInterface;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;

public class MessagingInterfaceImpl implements MessagingInterface, AutoCloseable {

    private final Closer mResourceHolder;
    private final KnownMessageTypes mMessageTypes;
    private final MessageService mMessageService;

    public MessagingInterfaceImpl(NetworkConfiguration.MessagingConfiguration configuration) {
        mResourceHolder = new Closer();
        mMessageTypes = new KnownMessageTypes();

        MessagingChannel channel;
        if (configuration.isServer) {
            ServerMessagingChannel channelImpl = new ServerMessagingChannel(configuration.address, mMessageTypes);
            mResourceHolder.add(channelImpl);

            ServerConnectionService service = new ServerConnectionService(channelImpl);
            try {
                service.start();
            } catch (ServiceException e) {
                throw new Error(e);
            }
            mResourceHolder.add(service);

            channel = channelImpl;
        } else {
            channel = new ClientMessagingChannel(configuration.address, mMessageTypes);
            mResourceHolder.add(channel);
        }

        mMessageService = new MessageService(channel);
        mResourceHolder.add(mMessageService);
    }

    @Override
    public void registerMessageType(MessageType type) {
        mMessageTypes.put(type);
    }

    @Override
    public MessageQueue getQueue() {
        return mMessageService;
    }

    @Override
    public MessageHandler getHandler() {
        return mMessageService;
    }

    @Override
    public void close() throws Exception {
        mResourceHolder.close();
    }
}
