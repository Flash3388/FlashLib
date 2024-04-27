package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class ServerChannelListener implements ServerMessagingChannel.Listener {

    private final EventController mEventController;
    private final Logger mLogger;

    ServerChannelListener(EventController eventController, Logger logger) {
        mEventController = eventController;
        mLogger = logger;
    }

    @Override
    public void onClientConnected(ChannelId clientId) {
        mLogger.debug("ServerChannel: received new client, alerting listeners");

        mEventController.fire(
                new NewClientEvent(clientId),
                NewClientEvent.class,
                ConnectionListener.class,
                ConnectionListener::onClientConnected
        );
    }

    @Override
    public void onClientDisconnected(ChannelId clientId) {
        mLogger.debug("ServerChannel: client disconnected, alerting listeners");

        mEventController.fire(
                new NewClientEvent(clientId),
                NewClientEvent.class,
                ConnectionListener.class,
                ConnectionListener::onClientDisconnected
        );
    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {
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

    @Override
    public void onMessageSendingFailed(ChannelId id, Message message, Throwable cause) {
        mLogger.error("Failed sending message (type={}) to client {}", message.getType().getKey(), id);
    }
}
