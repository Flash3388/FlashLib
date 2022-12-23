package com.flash3388.flashlib.app.net;

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
        final boolean isEnabled;
        final boolean broadcastModeEnabled;

        private HfcsConfiguration(boolean isEnabled, boolean broadcastModeEnabled) {
            this.isEnabled = isEnabled;
            this.broadcastModeEnabled = broadcastModeEnabled;
        }

        public static HfcsConfiguration disabled() {
            return new HfcsConfiguration(false, false);
        }

        public static HfcsConfiguration broadcastMode() {
            return new HfcsConfiguration(true, true);
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
