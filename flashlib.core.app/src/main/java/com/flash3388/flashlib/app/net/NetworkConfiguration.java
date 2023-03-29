package com.flash3388.flashlib.app.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class NetworkConfiguration implements NetworkingMode {

    public static class ObjectStorageConfiguration {
        final boolean isEnabled;
        final boolean isPrimaryNode;
        final String primaryNodeAddress;

        private ObjectStorageConfiguration(boolean isEnabled, boolean isPrimaryNode, String primaryNodeAddress) {
            this.isEnabled = isEnabled;
            this.isPrimaryNode = isPrimaryNode;
            this.primaryNodeAddress = primaryNodeAddress;
        }

        public static ObjectStorageConfiguration disabled() {
            return new ObjectStorageConfiguration(false, false, null);
        }

        public static ObjectStorageConfiguration primaryNode() {
            return new ObjectStorageConfiguration(true, true, null);
        }

        public static ObjectStorageConfiguration secondaryNode(String primaryNodeAddress) {
            return new ObjectStorageConfiguration(true, false, primaryNodeAddress);
        }
    }

    public static class HfcsConfiguration {
        static final int INVALID_PORT = -1;

        final boolean isEnabled;
        final boolean replyToSenderModeEnabled;
        final boolean specificTargetModeEnabled;
        final boolean multicastModeEnabled;
        final boolean broadcastModeEnabled;
        final SocketAddress specificTargetAddress;
        final NetworkInterface multicastInterface;
        final InetAddress multicastGroup;
        final int remotePort;
        final int bindPort;

        private HfcsConfiguration(boolean isEnabled,
                                  boolean replyToSenderModeEnabled,
                                  boolean specificTargetModeEnabled,
                                  boolean multicastModeEnabled,
                                  boolean broadcastModeEnabled,
                                  SocketAddress specificTargetAddress,
                                  NetworkInterface multicastInterface,
                                  InetAddress multicastGroup,
                                  int remotePort,
                                  int bindPort) {
            this.isEnabled = isEnabled;
            this.replyToSenderModeEnabled = replyToSenderModeEnabled;
            this.specificTargetModeEnabled = specificTargetModeEnabled;
            this.multicastModeEnabled = multicastModeEnabled;
            this.broadcastModeEnabled = broadcastModeEnabled;
            this.specificTargetAddress = specificTargetAddress;
            this.multicastInterface = multicastInterface;
            this.multicastGroup = multicastGroup;
            this.remotePort = remotePort;
            this.bindPort = bindPort;
        }

        public static HfcsConfiguration disabled() {
            return new HfcsConfiguration(
                    false,
                    false,
                    false,
                    false,
                    false,
                    null,
                    null,
                    null,
                    INVALID_PORT,
                    INVALID_PORT);
        }

        public static HfcsConfiguration replyToSenderMode(int bindPort) {
            return new HfcsConfiguration(
                    true,
                    true,
                    false,
                    false,
                    false,
                    null,
                    null,
                    null,
                    INVALID_PORT,
                    bindPort);
        }

        public static HfcsConfiguration replyToSenderMode() {
            return replyToSenderMode(INVALID_PORT);
        }

        public static HfcsConfiguration specificTargetMode(int bindPort, SocketAddress remote) {
            return new HfcsConfiguration(
                    true,
                    false,
                    true,
                    false,
                    false,
                    remote,
                    null,
                    null,
                    INVALID_PORT,
                    bindPort);
        }

        public static HfcsConfiguration specificTargetMode(SocketAddress remote) {
            return specificTargetMode(INVALID_PORT, remote);
        }

        public static HfcsConfiguration multicastMode(int bindPort,
                                                      NetworkInterface networkInterface,
                                                      InetAddress group,
                                                      int remotePort) {
            return new HfcsConfiguration(
                    true,
                    false,
                    false,
                    true,
                    false,
                    null,
                    networkInterface,
                    group,
                    remotePort,
                    bindPort);
        }

        public static HfcsConfiguration broadcastMode(int bindPort, int remotePort) {
            return new HfcsConfiguration(
                    true,
                    false,
                    false,
                    false,
                    true,
                    null,
                    null,
                    null,
                    remotePort,
                    bindPort);
        }
    }

    private final boolean mEnabled;
    private final ObjectStorageConfiguration mObjectStorageConfiguration;
    private final HfcsConfiguration mHfcsConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 ObjectStorageConfiguration objectStorageConfiguration,
                                 HfcsConfiguration hfcsConfiguration) {
        mEnabled = enabled;
        mObjectStorageConfiguration = objectStorageConfiguration;
        mHfcsConfiguration = hfcsConfiguration;
    }

    private NetworkConfiguration() {
        this(false, null, null);
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration();
    }

    public static NetworkConfiguration enabled(ObjectStorageConfiguration objectStorageConfiguration,
                                               HfcsConfiguration hfcsConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration, hfcsConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObjectStorageConfiguration != null && mObjectStorageConfiguration.isEnabled;
    }

    @Override
    public boolean isHfcsEnabled() {
        return mHfcsConfiguration != null && mHfcsConfiguration.isEnabled;
    }

    public ObjectStorageConfiguration getObjectStorageConfiguration() {
        return mObjectStorageConfiguration;
    }

    public HfcsConfiguration getHfcsConfiguration() {
        return mHfcsConfiguration;
    }
}
