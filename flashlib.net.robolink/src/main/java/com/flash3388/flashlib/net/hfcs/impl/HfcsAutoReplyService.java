package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.AutoReplyingUdpMessagingChannel;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.HashMap;
import java.util.Map;

public class HfcsAutoReplyService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsAutoReplyService(InstanceId ourId, Clock clock) {
        super(ourId, clock);

        mChannel = new AutoReplyingUdpMessagingChannel(
                Constants.DEFAULT_PORT,
                ourId,
                mMessageWriter,
                mMessageReader,
                clock,
                LOGGER
        );
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsAutoReply-UpdateTask",
                new BasicUpdateTask(mChannel, LOGGER,
                        new BasicChannelUpdateHandler(mEventController, LOGGER)));
        tasks.put("HfcsAutoReply-WriteTask",
                new BasicWriteTask(mOutDataQueue, mChannel, LOGGER));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
