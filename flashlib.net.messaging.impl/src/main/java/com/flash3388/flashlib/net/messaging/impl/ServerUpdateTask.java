package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;
import org.slf4j.Logger;

public class ServerUpdateTask implements Runnable {

    private final MessagingServerChannel mChannel;
    private final Logger mLogger;

    public ServerUpdateTask(MessagingServerChannel channel, Logger logger) {
        mChannel = channel;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mChannel.handleUpdates();
            } catch (InterruptedException e) {
                break;
            } catch (TimeoutException e) {
                // no need to do anything
            } catch (Throwable t) {
                mLogger.error("Error in AcceptTask", t);
            }
        }
    }
}
