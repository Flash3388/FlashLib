package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.udp.AutoReplyingUdpChannel;
import com.flash3388.flashlib.net.channels.udp.UdpChannel;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HfcsUnicastService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsUnicastService(InstanceId ourId, Clock clock, int bindPort, SocketAddress remote) {
        super(ourId, clock);


        mChannel = new BasicMessagingChannel(
                (onConn)-> new UdpChannel(remote, bindPort, LOGGER, onConn),
                ourId,
                LOGGER,
                getMessageTypes()
        );
    }

    public HfcsUnicastService(InstanceId ourId, Clock clock, SocketAddress remote) {
        this(ourId, clock, Constants.DEFAULT_PORT, remote);
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
