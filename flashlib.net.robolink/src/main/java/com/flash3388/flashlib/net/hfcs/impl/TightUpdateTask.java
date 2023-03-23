package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.message.ConfigurableTargetUdpMessagingChannel;
import com.flash3388.flashlib.net.message.MessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;

public class TightUpdateTask implements Runnable {

    private final TightConnectionPackage mConnectionPackage;
    private final MessagingChannel mChannel;
    private final Logger mLogger;

    public TightUpdateTask(TightConnectionPackage connectionPackage,
                      ConfigurableTargetUdpMessagingChannel channel,
                      Logger logger) {
        mConnectionPackage = connectionPackage;
        mChannel = channel;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                if (mConnectionPackage.isCurrentRemoteTimerExpired()) {
                    mConnectionPackage.switchRemote();
                }

                mChannel.handleUpdates(mConnectionPackage);
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
