package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {

    private final MessagingChannel mChannel;
    private final BlockingQueue<PendingWriteMessage> mQueue;
    private final Logger mLogger;

    public WriteTask(MessagingChannel channel, BlockingQueue<PendingWriteMessage> queue, Logger logger) {
        mChannel = channel;
        mQueue = queue;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                PendingWriteMessage message = mQueue.take();
                mChannel.write(message.getMessageType(), message.getMessage());
            } catch (IOException e) {
                mLogger.debug("Error processing changes", e);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
