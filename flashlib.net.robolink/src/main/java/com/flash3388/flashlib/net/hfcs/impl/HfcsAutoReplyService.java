package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.udp.AutoReplyingUdpChannel;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.HashMap;
import java.util.Map;

public class HfcsAutoReplyService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsAutoReplyService(InstanceId ourId, Clock clock, int bindPort) {
        super(ourId, clock);


        mChannel = new BasicMessagingChannel(
                (onConn)-> new AutoReplyingUdpChannel(bindPort, LOGGER, onConn),
                ourId,
                LOGGER,
                getMessageTypes()
        );
    }

    public HfcsAutoReplyService(InstanceId ourId, Clock clock) {
        this(ourId, clock, Constants.DEFAULT_PORT);
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsAutoReply-UpdateTask",
                new BasicUpdateTask(mChannel, LOGGER,
                        new BasicChannelUpdateHandler(mEventController, LOGGER)));
        tasks.put("HfcsAutoReply-WriteTask",
                new BasicWriteTask(mOutDataQueue, mChannel, LOGGER, new HfcsMessageType(mInDataTypes)));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
