package com.flash3388.flashlib.app.net;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
        final boolean broadcastModeEnabled;
        final boolean replyToSenderModeEnabled;
        final boolean specificTargetModeEnabled;
        final Collection<SocketAddress> specificTargetAddress;
        final int bindPort;

        private HfcsConfiguration(boolean isEnabled, boolean broadcastModeEnabled, boolean replyToSenderModeEnabled, boolean specificTargetModeEnabled, Collection<SocketAddress> specificTargetAddress, int bindPort) {
            this.isEnabled = isEnabled;
            this.broadcastModeEnabled = broadcastModeEnabled;
            this.replyToSenderModeEnabled = replyToSenderModeEnabled;
            this.specificTargetModeEnabled = specificTargetModeEnabled;
            this.specificTargetAddress = specificTargetAddress;
            this.bindPort = bindPort;
        }

        public static HfcsConfiguration disabled() {
            return new HfcsConfiguration(
                    false,
                    false,
                    false,
                    false, null,
                    INVALID_PORT);
        }

        public static HfcsConfiguration broadcastMode() {
            return new HfcsConfiguration(
                    true,
                    true,
                    false,
                    false, null,
                    INVALID_PORT);
        }

        public static HfcsConfiguration replyToSenderMode(int bindPort) {
            return new HfcsConfiguration(
                    true,
                    false,
                    true,
                    false, null,
                    bindPort);
        }

        public static HfcsConfiguration replyToSenderMode() {
            return replyToSenderMode(INVALID_PORT);
        }

        public static HfcsConfiguration specificTargetMode(Collection<? extends SocketAddress> targetAddress) {
            return new HfcsConfiguration(
                    true,
                    false,
                    false,
                    true, new ArrayList<>(targetAddress),
                    INVALID_PORT);
        }

        public static HfcsConfiguration specificTargetMode(SocketAddress... targetAddress) {
            return specificTargetMode(Arrays.asList(targetAddress));
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
