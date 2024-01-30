package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class ServerChannelListener implements ServerMessagingChannel.Listener {

    private final WeakReference<ServerMessagingChannel> mChannel;
    private final EventController mEventController;
    private final Logger mLogger;

    ServerChannelListener(ServerMessagingChannel channel, EventController eventController, Logger logger) {
        mChannel = new WeakReference<>(channel);
        mEventController = eventController;
        mLogger = logger;
    }

    @Override
    public void onClientConnected(InstanceId clientId) {
        mLogger.debug("ServerChannel: received new client, alerting listeners");

        mEventController.fire(
                new NewClientEvent(clientId),
                NewClientEvent.class,
                ConnectionListener.class,
                ConnectionListener::onClientConnected
        );
    }

    @Override
    public void onClientDisconnected(InstanceId clientId) {

    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {
        if (message.getType().equals(PingMessage.TYPE)) {
            mLogger.debug("ServerChannel: received ping message, responding");

            // resend the ping message with the same time
            ServerMessagingChannel channel = mChannel.get();
            if (channel != null) {
                channel.queue(message);
            }
        } else {
            mLogger.debug("ServerChannel: received message, alerting listeners");

            MessageMetadata metadata = new MessageMetadataImpl(
                    header.getSender(),
                    header.getSendTime(),
                    message.getType());

            mEventController.fire(
                    new NewMessageEvent(metadata, message),
                    NewMessageEvent.class,
                    MessageListener.class,
                    MessageListener::onNewMessage
            );
        }
    }

    @Override
    public void onMessageSendingFailed(Message message) {
        // todo: what to do??
    }
}
