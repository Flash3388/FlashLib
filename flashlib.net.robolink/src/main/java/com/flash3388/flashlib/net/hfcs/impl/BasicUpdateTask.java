package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;

public class BasicUpdateTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;

    public BasicUpdateTask(MessagingChannel channel,
                           Logger logger,
                           MessagingChannel.UpdateHandler handler) {
        mChannel = channel;
        mLogger = logger;
        mHandler = handler;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mChannel.processUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error in UpdateTask", e);
            }
        }
    }
}
