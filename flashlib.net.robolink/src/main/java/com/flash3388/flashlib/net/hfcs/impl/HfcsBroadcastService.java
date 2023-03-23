package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.BroadcastUdpMessagingChannel;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HfcsBroadcastService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsBroadcastService(InstanceId ourId, Clock clock, Logger logger) {
        super(ourId, clock, logger);

        mChannel = new BroadcastUdpMessagingChannel(
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
        tasks.put("HfcsBroadcast-UpdateTask",
                new BasicUpdateTask(mChannel, mLogger, new BasicChannelUpdateHandler(mEventController, mLogger)));
        tasks.put("HfcsBroadcast-WriteTask",
                new BasicWriteTask(mOutDataQueue, mChannel, mLogger));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
