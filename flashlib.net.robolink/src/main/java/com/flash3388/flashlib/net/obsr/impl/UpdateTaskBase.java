package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public abstract class UpdateTaskBase implements Runnable {

    private static final long SLEEP_TIME_MS = 5;

    protected final Logger mLogger;
    private final MessagingChannel mChannel;
    private final BlockingQueue<PendingWriteMessage> mWriteQueue;


    protected UpdateTaskBase(Logger logger,
                             MessagingChannel channel,
                             BlockingQueue<PendingWriteMessage> writeQueue) {
        mLogger = logger;
        mChannel = channel;
        mWriteQueue = writeQueue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                processUpdates();
                processPendingMessages();

                //noinspection BusyWait
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    protected final void processPendingMessages() throws InterruptedException {
        mLogger.trace("Processing pending messages");

        int size = mWriteQueue.size();
        while (size-- > 0) {
            try {
                PendingWriteMessage message = mWriteQueue.poll();
                if (message == null) {
                    break;
                }

                mLogger.debug("Sending data of type {}", message.getMessageType().getKey());
                mChannel.write(message.getMessageType(), message.getMessage());
            } catch (IOException e) {
                mLogger.debug("Error processing changes", e);
            }
        }
    }

    protected abstract void processUpdates();
}
