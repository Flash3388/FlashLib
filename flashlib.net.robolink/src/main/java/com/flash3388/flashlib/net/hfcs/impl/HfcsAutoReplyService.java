package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.AutoReplyingUdpMessagingChannel;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HfcsAutoReplyService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsAutoReplyService(InstanceId ourId, Clock clock, Logger logger) {
        super(ourId, clock, logger);

        mChannel = new AutoReplyingUdpMessagingChannel(
                Constants.DEFAULT_PORT,
                ourId,
                mMessageWriter,
                mMessageReader,
                clock,
                logger
        );
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsAutoReply-UpdateTask",
                new BasicUpdateTask(mChannel, mLogger, new BasicChannelUpdateHandler(mEventController, mLogger)));
        tasks.put("HfcsAutoReply-WriteTask",
                new BasicWriteTask(mOutDataQueue, mChannel, mLogger));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
