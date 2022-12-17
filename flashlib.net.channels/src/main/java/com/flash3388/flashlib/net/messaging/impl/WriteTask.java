package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {

    private final MessagingChannel mChannel;
    private final BlockingQueue<Message> mMessageQueue;
    private final Logger mLogger;

    WriteTask(MessagingChannel channel, BlockingQueue<Message> messageQueue, Logger logger) {
        mChannel = channel;
        mMessageQueue = messageQueue;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mLogger.debug("WriteTask waiting for connection");
                mChannel.waitForConnection();

                mLogger.debug("WriteTask waiting for new message to send");
                Message message = mMessageQueue.take();

                mLogger.debug("WriteTask sending message with type key={}", message.getType().getKey());
                mChannel.write(message);
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
