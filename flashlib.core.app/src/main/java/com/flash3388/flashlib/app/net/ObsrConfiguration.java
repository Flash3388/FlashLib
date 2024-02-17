package com.flash3388.flashlib.app.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

public class ObsrConfiguration {

    private static final int DEFAULT_SERVER_PORT = 4156;

    final boolean isEnabled;
    final boolean isLocal;
    final boolean isPrimaryMode;
    final SocketAddress serverAddress;

    private ObsrConfiguration(boolean isEnabled, boolean isLocal, boolean isPrimaryMode, SocketAddress serverAddress) {
        this.isEnabled = isEnabled;
        this.isLocal = isLocal;
        this.isPrimaryMode = isPrimaryMode;
        this.serverAddress = serverAddress;
    }

    private ObsrConfiguration() {
        this(false, false, false, null);
    }

    public static ObsrConfiguration disabled() {
        return new ObsrConfiguration();
    }

    public static ObsrConfiguration primaryNode(SocketAddress bindAddress) {
        Objects.requireNonNull(bindAddress, "bindAddress");
        return new ObsrConfiguration(true, false, true, bindAddress);
    }

    public static ObsrConfiguration primaryNode() {
        return primaryNode(new InetSocketAddress("0.0.0.0", DEFAULT_SERVER_PORT));
    }

    public static ObsrConfiguration secondaryNode(SocketAddress primaryNodeAddress) {
        Objects.requireNonNull(primaryNodeAddress, "primaryNodeAddress");
        return new ObsrConfiguration(true, false, false, primaryNodeAddress);
    }

    public static ObsrConfiguration secondaryNode(String primaryNodeAddress) {
        return secondaryNode(new InetSocketAddress(primaryNodeAddress, DEFAULT_SERVER_PORT));
    }

    public static ObsrConfiguration localOnly() {
        return new ObsrConfiguration(true, true, false, null);
    }
}
