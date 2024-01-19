package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HfcsServiceImpl extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsServiceImpl(InstanceId ourId, Clock clock, Function<Runnable, ? extends NetChannel> channelCreator) {
        super(ourId, clock);


        mChannel = new BasicMessagingChannelImpl(
                channelCreator,
                ourId,
                LOGGER,
                getMessageTypes()
        );
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsAutoReply-UpdateTask",
                new BasicUpdateTask(
                        mChannel,
                        mClock,
                        LOGGER,
                        new BasicChannelUpdateHandler(mEventController, mClock, LOGGER, mInDataNodes),
                        mInDataNodes.values(),
                        mEventController,
                        mOutDataQueue,
                        new HfcsMessageType(mInDataTypes)));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
