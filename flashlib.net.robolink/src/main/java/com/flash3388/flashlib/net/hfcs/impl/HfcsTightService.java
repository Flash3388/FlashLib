package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.message.ConfigurableTargetUdpMessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HfcsTightService extends HfcsServiceBase {

    private static final Time RECEIVE_TIMER_EXPIRATION = Time.milliseconds(500);

    private final ConfigurableTargetUdpMessagingChannel mChannel;
    private final TightConnectionPackage mConnectionPackage;

    public HfcsTightService(Collection<SocketAddress> possibleAddress,
                            InstanceId ourId,
                            Clock clock,
                            Logger logger) {
        super(ourId, clock, logger);

        mChannel = new ConfigurableTargetUdpMessagingChannel(
                Constants.DEFAULT_PORT,
                ourId,
                mMessageWriter,
                mMessageReader,
                clock,
                logger
        );

        mConnectionPackage = new TightConnectionPackage(
                mEventController,
                clock,
                logger,
                new ArrayDeque<>(possibleAddress),
                mChannel,
                RECEIVE_TIMER_EXPIRATION
        );
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsAutoReply-UpdateTask",
                new TightUpdateTask(mConnectionPackage, mChannel, mLogger));
        tasks.put("HfcsAutoReply-WriteTask",
                new BasicWriteTask(mOutDataQueue, mChannel, mLogger));
        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }
}
