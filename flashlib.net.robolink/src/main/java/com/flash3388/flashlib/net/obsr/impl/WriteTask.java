package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {

    private final BlockingQueue<PendingWriteMessage> mQueue;
    private final MessagingChannel mChannel;
    private final Logger mLogger;

    public WriteTask(BlockingQueue<PendingWriteMessage> queue,
                     MessagingChannel channel,
                     Logger logger) {
        mQueue = queue;
        mChannel = channel;
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
