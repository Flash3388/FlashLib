package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;

public class ReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final EventController mEventController;
    private final Logger mLogger;

    ReadTask(MessagingChannel channel, EventController eventController, Logger logger) {
        mChannel = channel;
        mEventController = eventController;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mLogger.debug("ReadTask waiting for connection");
                mChannel.waitForConnection();

                mLogger.debug("ReadTask waiting for message");
                Message message = mChannel.read();

                mLogger.debug("ReadTask received message with type key={}", message.getType().getKey());
                mEventController.fire(
                        new NewMessageEvent(message),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage
                );
            } catch (InterruptedException e) {
                break;
            } catch (TimeoutException e) {
                // no need to do anything
            } catch (Throwable t) {
                mLogger.error("Error in ReadTask", t);
            }
        }
    }
}
