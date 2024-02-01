package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.net.channels.messsaging.BaseMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;

public class UpdateTask implements Runnable {

    private final BaseMessagingChannel mChannel;

    public UpdateTask(MessagingChannel channel) {
        mChannel = channel;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

        }
    }
}
