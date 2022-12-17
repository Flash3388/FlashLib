package com.flash3388.flashlib.robot.net.impl;

import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.packets.InboundPacketsReceiver;
import com.flash3388.flashlib.net.packets.PacketsSender;
import com.flash3388.flashlib.net.robolink.RemotesStorage;
import com.flash3388.flashlib.net.packets.impl.RoboLinkService;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;
import com.flash3388.flashlib.robot.net.RoboLinkInterface;
import com.flash3388.flashlib.time.Clock;
import com.notifier.Controllers;
import org.slf4j.Logger;

import java.util.UUID;

public class RoboLinkInterfaceImpl implements RoboLinkInterface, AutoCloseable {

    private final RoboLinkService mService;

    public RoboLinkInterfaceImpl(NetworkConfiguration.RoboLinkConfiguration configuration, Clock clock, Logger logger) {
        mService = new RoboLinkService(UUID.randomUUID().toString(), Controllers.newSyncExecutionController(),
                clock, logger);

        try {
            mService.start();
        } catch (ServiceException e) {
            throw new Error(e);
        }
    }

    @Override
    public InboundPacketsReceiver getReceiver() {
        return mService;
    }

    @Override
    public PacketsSender getSender() {
        return mService;
    }

    @Override
    public RemotesStorage getStorage() {
        return mService;
    }

    @Override
    public void close() throws Exception {
        mService.close();
    }
}
