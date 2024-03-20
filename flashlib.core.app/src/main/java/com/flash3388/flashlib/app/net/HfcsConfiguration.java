package com.flash3388.flashlib.app.net;

import java.net.SocketAddress;
import java.util.Objects;

public class HfcsConfiguration {

    final boolean isEnabled;
    final boolean isTargeted;
    final SocketAddress localAddress;
    final SocketAddress remoteAddress;

    private HfcsConfiguration(boolean isEnabled, boolean isTargeted, SocketAddress localAddress, SocketAddress remoteAddress) {
        this.isEnabled = isEnabled;
        this.isTargeted = isTargeted;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public static HfcsConfiguration disabled() {
        return new HfcsConfiguration(false, false, null, null);
    }

    public static HfcsConfiguration targeted(SocketAddress localAddress, SocketAddress remoteAddress) {
        Objects.requireNonNull(localAddress, "localAddress");
        Objects.requireNonNull(remoteAddress, "remoteAddress");
        return new HfcsConfiguration(true, true, localAddress, remoteAddress);
    }

    public static HfcsConfiguration replying(SocketAddress localAddress) {
        Objects.requireNonNull(localAddress, "localAddress");
        return new HfcsConfiguration(true, false, localAddress, null);
    }
}
