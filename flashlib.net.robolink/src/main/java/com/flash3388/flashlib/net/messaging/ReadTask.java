package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageAndType;
import com.flash3388.flashlib.net.channels.messsaging.MessageInfo;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;

    public ReadTask(MessagingChannel channel, Logger logger,
                    EventController eventController, Clock clock) {
        mChannel = channel;
        mLogger = logger;
        mHandler = new MessagingChannel.UpdateHandler() {
            @Override
            public void onNewMessage(MessageInfo info, InMessage message) {
                eventController.fire(
                        new NewMessageEvent(
                                new MessageMetadataImpl(
                                        info.getSender(),
                                        clock.currentTime(),
                                        info.getType()
                                ),
                                message
                        ),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage
                );
            }

            @Override
            public Optional<List<MessageAndType>> getMessageForNewClient() {
                return Optional.empty();
            }
        };
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
}
