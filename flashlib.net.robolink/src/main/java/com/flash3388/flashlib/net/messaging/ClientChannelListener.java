package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

class ClientChannelListener implements MessagingChannel.Listener {

    private final PingContext mPingContext;
    private final EventController mEventController;
    private final ServerClock mClock;
    private final Logger mLogger;

    ClientChannelListener(PingContext pingContext, EventController eventController, ServerClock clock, Logger logger) {
        mPingContext = pingContext;
        mEventController = eventController;
        mClock = clock;
        mLogger = logger;
    }

    @Override
    public void onConnect() {
        mPingContext.onConnect();
    }

    @Override
    public void onDisconnect() {
        mPingContext.onDisconnect();
    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {
        if (message.getType().equals(PingMessage.TYPE)) {
            PingMessage pingMessage = (PingMessage) message;
            mPingContext.onPingResponse(header, pingMessage);
        } else {
            mLogger.debug("ClientChannel: received message, alerting listeners");

            // fixed timestamp to client-only times
            Time sendTime = mClock.adjustToClientTime(header.getSendTime());

            MessageMetadata metadata = new MessageMetadataImpl(
                    header.getSender(),
                    sendTime,
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
