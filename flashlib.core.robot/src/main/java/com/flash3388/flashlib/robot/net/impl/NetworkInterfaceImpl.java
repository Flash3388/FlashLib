package com.flash3388.flashlib.robot.net.impl;

import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;
import com.flash3388.flashlib.robot.net.NetworkInterface;
import com.flash3388.flashlib.robot.net.NetworkingMode;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public class NetworkInterfaceImpl implements NetworkInterface, AutoCloseable {

    private final NetworkingMode mMode;
    private final Closer mCloser;

    public NetworkInterfaceImpl(NetworkConfiguration configuration, Clock clock, Logger logger) {
        mMode = configuration;
        mCloser = Closer.empty();
    }

    @Override
    public NetworkingMode getMode() {
        return mMode;
    }

    @Override
    public void close() throws Exception {
        mCloser.close();
    }
}
