package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class ServerReadTask implements Runnable {

    private final ServerMessagingChannel mChannel;
    private final Logger mLogger;
    private final ServerMessagingChannel.UpdateHandler mHandler;

    ServerReadTask(Messenger messenger,
                   ServerMessagingChannel channel,
                   EventController eventController,
                   Logger logger) {
        mChannel = channel;
        mLogger = logger;
        mHandler = new UpdateHandlerImpl(messenger, eventController, logger);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mChannel.processUpdates(mHandler);
            } catch (Throwable t) {
                mLogger.error("ServerReadTask: Error processing updates", t);
            }
        }
    }

    private static class UpdateHandlerImpl implements ServerMessagingChannel.UpdateHandler {

        private final WeakReference<Messenger> mMessenger;
        private final EventController mEventController;
        private final Logger mLogger;

        private UpdateHandlerImpl(Messenger messenger,
                                  EventController eventController,
                                  Logger logger) {
            mMessenger = new WeakReference<>(messenger);
            mEventController = eventController;
            mLogger = logger;
        }

        @Override
        public void onClientConnected(InstanceId clientId) {
            mLogger.debug("ServerReadTask: received new client, alerting listeners");

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
            MessageMetadata metadata = new MessageMetadataImpl(
                    header.getSender(),
                    header.getSendTime(),
                    message.getType());

            if (message.getType().equals(PingMessage.TYPE)) {
                mLogger.debug("ServerReadTask: received ping message, responding");

                // resend the ping message with the same time
                Messenger messenger = mMessenger.get();
                if (messenger != null) {
                    messenger.send(message);
                }
            } else {
                mLogger.debug("ServerReadTask: received message, alerting listeners");

                mEventController.fire(
                        new NewMessageEvent(metadata, message),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage
                );
            }
        }
    }
}
