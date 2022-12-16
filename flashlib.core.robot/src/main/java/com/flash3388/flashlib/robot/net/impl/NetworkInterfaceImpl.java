package com.flash3388.flashlib.robot.net.impl;

import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.robot.net.MessagingInterface;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;
import com.flash3388.flashlib.robot.net.NetworkInterface;
import com.flash3388.flashlib.robot.net.NetworkingMode;
import org.slf4j.Logger;

public class NetworkInterfaceImpl implements NetworkInterface, AutoCloseable {

    private final NetworkingMode mMode;
    private final MessagingInterface mMessagingInterface;
    private final Closer mCloser;

    public NetworkInterfaceImpl(NetworkConfiguration configuration, Logger logger) {
        mMode = configuration;
        mCloser = Closer.empty();

        if (mMode.isNetworkingEnabled() && mMode.isMessagingSupported()) {
            MessagingInterfaceImpl impl = new MessagingInterfaceImpl(configuration.getMessagingConfiguration(), logger);
            mCloser.add(impl);
            mMessagingInterface = impl;
        } else {
            mMessagingInterface = null;
        }
    }

    @Override
    public NetworkingMode getMode() {
        return mMode;
    }

    @Override
    public MessagingInterface getMessaging() {
        if (!mMode.isMessagingSupported()) {
            throw new UnsupportedOperationException();
        }

        return mMessagingInterface;
    }

    @Override
    public void close() throws Exception {
        mCloser.close();
    }
}
