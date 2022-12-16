package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;

import java.io.IOException;

public class AcceptThread implements Runnable {

    private final MessagingServerChannel mChannel;

    public AcceptThread(MessagingServerChannel channel) {
        mChannel = channel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mChannel.handleNewConnections();
            } catch (IOException | TimeoutException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
