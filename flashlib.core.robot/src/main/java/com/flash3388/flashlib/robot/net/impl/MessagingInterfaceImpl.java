package com.flash3388.flashlib.robot.net.impl;

import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageReceiver;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.impl.ClientMessagingChannel;
import com.flash3388.flashlib.net.messaging.impl.MessagingService;
import com.flash3388.flashlib.net.messaging.impl.ServerMessagingChannel;
import com.flash3388.flashlib.robot.net.MessagingInterface;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;
import com.notifier.Controllers;
import org.slf4j.Logger;

public class MessagingInterfaceImpl implements MessagingInterface, AutoCloseable {

    private final Closer mResourceHolder;
    private final KnownMessageTypes mMessageTypes;
    private final MessagingService mMessagingService;

    public MessagingInterfaceImpl(NetworkConfiguration.MessagingConfiguration configuration, Logger logger) {
        mResourceHolder = new Closer();
        mMessageTypes = new KnownMessageTypes();

        if (configuration.isServer) {
            ServerMessagingChannel channel = new ServerMessagingChannel(configuration.address, mMessageTypes);
            mResourceHolder.add(channel);
            mMessagingService = MessagingService.server(channel, Controllers.newSyncExecutionController(), logger);
            mResourceHolder.add(mMessagingService);
        } else {
            ClientMessagingChannel channel = new ClientMessagingChannel(configuration.address, mMessageTypes);
            mResourceHolder.add(channel);
            mMessagingService = MessagingService.client(channel, Controllers.newSyncExecutionController(), logger);
            mResourceHolder.add(mMessagingService);
        }

        try {
            mMessagingService.start();
        } catch (ServiceException e) {
            throw new Error(e);
        }
    }

    @Override
    public void registerMessageType(MessageType type) {
        mMessageTypes.put(type);
    }

    @Override
    public MessageQueue getQueue() {
        return mMessagingService;
    }

    @Override
    public MessageReceiver getReceiver() {
        return mMessagingService;
    }

    @Override
    public void close() throws Exception {
        mResourceHolder.close();
    }
}
