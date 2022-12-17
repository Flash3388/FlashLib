package com.flash3388.flashlib.robot.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

public class NetworkConfiguration implements NetworkingMode {

    public static class MessagingConfiguration {
        public final boolean isEnabled;
        public final boolean isServer;
        public final SocketAddress address;

        private MessagingConfiguration(boolean isEnabled, boolean isServer, SocketAddress address) {
            this.isEnabled = isEnabled;
            this.isServer = isServer;
            this.address = address;
        }

        public static MessagingConfiguration disabled() {
            return new MessagingConfiguration(false, false, null);
        }

        public static MessagingConfiguration server(int port) {
            return new MessagingConfiguration(true, true, new InetSocketAddress("0.0.0.0", port));
        }

        public static MessagingConfiguration client(String serverHost, int serverPort) {
            return new MessagingConfiguration(true, false, new InetSocketAddress(serverHost, serverPort));
        }
    }

    public static class RoboLinkConfiguration {
        public final boolean isEnabled;

        public RoboLinkConfiguration(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    private final boolean mEnabled;
    private final MessagingConfiguration mMessagingConfiguration;
    private final RoboLinkConfiguration mRoboLinkConfiguration;

    private NetworkConfiguration(boolean enabled, MessagingConfiguration messagingConfiguration, RoboLinkConfiguration roboLinkConfiguration) {
        mEnabled = enabled;
        mMessagingConfiguration = messagingConfiguration;
        mRoboLinkConfiguration = roboLinkConfiguration;
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration(false, null, null);
    }

    public static NetworkConfiguration withMessaging(MessagingConfiguration configuration) {
        Objects.requireNonNull(configuration, "MessagingConfiguration should not be null");
        return new NetworkConfiguration(true, configuration, null);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isMessagingSupported() {
        return mMessagingConfiguration != null && mMessagingConfiguration.isEnabled;
    }

    @Override
    public boolean isRoboLinkSupported() {
        return mRoboLinkConfiguration != null && mRoboLinkConfiguration.isEnabled;
    }

    public MessagingConfiguration getMessagingConfiguration() {
        return mMessagingConfiguration;
    }

    public RoboLinkConfiguration getRoboLinkConfiguration() {
        return mRoboLinkConfiguration;
    }
}
