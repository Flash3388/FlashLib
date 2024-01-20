package com.flash3388.flashlib.app.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ObsrConfiguration {

    private static final int DEFAULT_SERVER_PORT = 4156;

    final boolean isEnabled;
    final boolean isPrimaryMode;
    final SocketAddress serverAddress;

    private ObsrConfiguration(boolean isEnabled, boolean isPrimaryMode, SocketAddress serverAddress) {
        this.isEnabled = isEnabled;
        this.isPrimaryMode = isPrimaryMode;
        this.serverAddress = serverAddress;
    }

    private ObsrConfiguration() {
        this(false, false, null);
    }

    public static ObsrConfiguration disabled() {
        return new ObsrConfiguration();
    }

    public static ObsrConfiguration primaryNode(SocketAddress bindAddress) {
        return new ObsrConfiguration(true, true, bindAddress);
    }

    public static ObsrConfiguration primaryNode() {
        return primaryNode(new InetSocketAddress("0.0.0.0", DEFAULT_SERVER_PORT));
    }

    public static ObsrConfiguration secondaryNode(SocketAddress primaryNodeAddress) {
        return new ObsrConfiguration(true, false, primaryNodeAddress);
    }

    public static ObsrConfiguration secondaryNode(String primaryNodeAddress) {
        return secondaryNode(new InetSocketAddress(primaryNodeAddress, DEFAULT_SERVER_PORT));
    }
}
