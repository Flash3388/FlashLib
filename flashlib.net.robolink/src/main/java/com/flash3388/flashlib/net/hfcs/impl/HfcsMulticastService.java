package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.udp.AutoReplyingUdpChannel;
import com.flash3388.flashlib.net.channels.udp.MulticastUdpChannel;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

public class HfcsMulticastService extends HfcsServiceBase {

    private final MessagingChannel mChannel;

    public HfcsMulticastService(InstanceId ourId, Clock clock,
                                int bindPort,
                                int remotePort,
                                NetworkInterface multicastInterface,
                                InetAddress multicastGroup) {
        super(ourId, clock);

        mChannel = new BasicMessagingChannel(
                (onConn)-> new MulticastUdpChannel(multicastInterface, multicastGroup,
                        bindPort, remotePort, LOGGER, onConn),
                ourId,
                LOGGER,
                getMessageTypes()
        );
    }

    public HfcsMulticastService(InstanceId ourId, Clock clock,
                                int remotePort,
                                NetworkInterface multicastInterface,
                                InetAddress multicastGroup) {
        this(ourId, clock, Constants.DEFAULT_PORT, remotePort, multicastInterface, multicastGroup);
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
