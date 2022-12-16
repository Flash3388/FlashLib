package com.flash3388.flashlib.robot.net.impl;

import com.flash3388.flashlib.robot.net.MessagingInterface;
import com.flash3388.flashlib.robot.net.NetworkConfiguration;
import com.flash3388.flashlib.robot.net.NetworkInterface;
import com.flash3388.flashlib.robot.net.NetworkingMode;

public class NetworkInterfaceImpl implements NetworkInterface {

    private final NetworkingMode mMode;
    private final MessagingInterface mMessagingInterface;

    public NetworkInterfaceImpl(NetworkConfiguration configuration) {
        mMode = configuration;

        if (mMode.isNetworkingEnabled() && mMode.isMessagingSupported()) {
            mMessagingInterface = new MessagingInterfaceImpl(configuration.getMessagingConfiguration());
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
}
