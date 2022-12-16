package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;
import org.slf4j.Logger;

import java.io.IOException;

public class AcceptThread implements Runnable {

    private final MessagingServerChannel mChannel;
    private final Logger mLogger;

    public AcceptThread(MessagingServerChannel channel, Logger logger) {
        mChannel = channel;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mChannel.handleNewConnections();
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
