package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.notifier.Event;
import com.notifier.EventController;

import java.io.IOException;

public class ReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final EventController mEventController;

    ReadTask(MessagingChannel channel, EventController eventController) {
        mChannel = channel;
        mEventController = eventController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (mChannel.establishConnection()) {
                    mEventController.fire(
                            null,
                            Event.class,
                            MessageListener.class,
                            MessageListener::onConnectionEstablished
                    );
                }

                Message message = mChannel.read();
                mEventController.fire(
                        new NewMessageEvent(message),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage
                );
            } catch (IOException | TimeoutException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
