package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.notifier.Event;
import com.notifier.EventController;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {

    private final MessagingChannel mChannel;
    private final EventController mEventController;
    private final BlockingQueue<Message> mMessageQueue;

    WriteTask(MessagingChannel channel, EventController eventController, BlockingQueue<Message> messageQueue) {
        mChannel = channel;
        mEventController = eventController;
        mMessageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = mMessageQueue.take();

                if (mChannel.establishConnection()) {
                    mEventController.fire(
                            null,
                            Event.class,
                            MessageListener.class,
                            MessageListener::onConnectionEstablished
                    );
                }

                mChannel.write(message);
            } catch (IOException | TimeoutException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
