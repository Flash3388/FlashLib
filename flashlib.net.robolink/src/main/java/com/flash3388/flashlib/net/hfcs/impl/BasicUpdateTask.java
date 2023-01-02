package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.message.MessagingChannel;
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
                mChannel.handleUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error in UpdateTask", e);
            } catch (InterruptedException e) {
                break;
            } catch (TimeoutException e) {
                // oh, well
            }
        }
    }
}
