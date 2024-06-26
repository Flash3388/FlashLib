package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

class ClientChannelListener implements MessagingChannel.Listener {

    private final EventController mEventController;
    private final Logger mLogger;

    ClientChannelListener(EventController eventController, Logger logger) {
        mEventController = eventController;
        mLogger = logger;
    }

    @Override
    public void onConnect() {
        mLogger.debug("ClientChannel: connected, alerting listeners");

        mEventController.fire(
                new EmptyEvent(),
                EmptyEvent.class,
                ConnectionListener.class,
                ConnectionListener::onConnected
        );
    }

    @Override
    public void onDisconnect() {
        mLogger.debug("ClientChannel: disconnected, alerting listeners");

        mEventController.fire(
                new EmptyEvent(),
                EmptyEvent.class,
                ConnectionListener.class,
                ConnectionListener::onDisconnected
        );
    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {
        mLogger.debug("ClientChannel: received message, alerting listeners");

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
    public void onMessageSendingFailed(Message message, Throwable cause) {
        mLogger.error("Failed sending message (type={})", message.getType().getKey());
    }
}
