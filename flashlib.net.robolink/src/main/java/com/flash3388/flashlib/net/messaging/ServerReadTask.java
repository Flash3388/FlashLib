package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.function.Consumer;

public class ServerReadTask implements Runnable {

    private final ServerMessagingChannel mChannel;
    private final Logger mLogger;
    private final ServerMessagingChannel.UpdateHandler mHandler;

    ServerReadTask(ServerMessagingChannel channel,
                   EventController eventController,
                   Clock clock,
                   Logger logger,
                   Consumer<InstanceId> onClientConnection) {
        mChannel = channel;
        mLogger = logger;
        mHandler = new UpdateHandlerImpl(eventController, clock, onClientConnection);
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

    private static class UpdateHandlerImpl implements ServerMessagingChannel.UpdateHandler {

        private final EventController mEventController;
        private final Clock mClock;
        private final Consumer<InstanceId> mOnClientConnection;

        private UpdateHandlerImpl(EventController eventController,
                                  Clock clock,
                                  Consumer<InstanceId> onClientConnection) {
            mEventController = eventController;
            mClock = clock;
            mOnClientConnection = onClientConnection;
        }

        @Override
        public void onClientConnected(InstanceId clientId) {
            if (mOnClientConnection != null) {
                mOnClientConnection.accept(clientId);
            }

            // MAKE SURE THAT OBSR STORAGE USES LATEST VALUE BY TIMESTAMP
        }

        @Override
        public void onClientDisconnected(InstanceId clientId) {

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
