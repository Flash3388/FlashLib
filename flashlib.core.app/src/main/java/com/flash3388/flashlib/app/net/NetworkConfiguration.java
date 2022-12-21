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

    private final boolean mEnabled;
    private final ObjectStorageConfiguration mObjectStorageConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 ObjectStorageConfiguration objectStorageConfiguration) {
        mEnabled = enabled;
        mObjectStorageConfiguration = objectStorageConfiguration;
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration(false, null);
    }

    public static NetworkConfiguration enabled(ObjectStorageConfiguration objectStorageConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObjectStorageConfiguration != null && mObjectStorageConfiguration.isEnabled;
    }

    public ObjectStorageConfiguration getObjectStorageConfiguration() {
        return mObjectStorageConfiguration;
    }
}
