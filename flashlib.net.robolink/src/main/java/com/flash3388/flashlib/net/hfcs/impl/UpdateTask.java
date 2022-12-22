package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;

public class UpdateTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;

    public UpdateTask(MessagingChannel channel, EventController eventController, Logger logger) {
        mChannel = channel;
        mLogger = logger;
        mHandler = new ChannelUpdateHandler(eventController, logger);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mChannel.handleUpdates(mHandler);
            } catch (IOException e) {
                mLogger.error("Error in updatetask", e);
            } catch (InterruptedException e) {
                break;
            } catch (TimeoutException e) {
                // oh, well
            }
        }
    }
}
