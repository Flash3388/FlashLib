package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {

    private final MessagingChannel mChannel;
    private final BlockingQueue<Message> mMessageQueue;

    WriteTask(MessagingChannel channel, BlockingQueue<Message> messageQueue) {
        mChannel = channel;
        mMessageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mChannel.waitForConnection();

                Message message = mMessageQueue.take();
                mChannel.write(message);
            } catch (IOException | TimeoutException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
