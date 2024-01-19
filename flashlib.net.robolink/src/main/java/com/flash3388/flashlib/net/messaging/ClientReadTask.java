package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;

public class ClientReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;

    ClientReadTask(MessagingChannel channel,
                   EventController eventController,
                   Clock clock,
                   Logger logger) {
        mChannel = channel;
        mLogger = logger;
        mHandler = new UpdateHandlerImpl(eventController, clock);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mChannel.processUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error processing changes", e);
            }
        }
    }

    private static class UpdateHandlerImpl implements MessagingChannel.UpdateHandler {

        private final EventController mEventController;
        private final Clock mClock;

        private UpdateHandlerImpl(EventController eventController, Clock clock) {
            mEventController = eventController;
            mClock = clock;
        }

        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onNewMessage(MessageHeader header, Message message) {
            Time now = mClock.currentTime();
            MessageMetadata metadata = new MessageMetadataImpl(header.getSender(), now, message.getType());

            mEventController.fire(
                    new NewMessageEvent(
                            metadata,
                            message
                    ),
                    NewMessageEvent.class,
                    MessageListener.class,
                    MessageListener::onNewMessage
            );
        }
    }
}
