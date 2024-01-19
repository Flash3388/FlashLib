package com.flash3388.flashlib.app.net;

public class NetworkConfiguration implements NetworkingMode {

    private final boolean mEnabled;
    private final Object mObsrConfiguration;
    private final Object mHfcsConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 Object obsrConfiguration,
                                 Object hfcsConfiguration) {
        mEnabled = enabled;
        mObsrConfiguration = obsrConfiguration;
        mHfcsConfiguration = hfcsConfiguration;
    }

    private NetworkConfiguration() {
        this(false, null, null);
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration();
    }

    public static NetworkConfiguration enabled(Object objectStorageConfiguration,
                                               Object hfcsConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration, hfcsConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObsrConfiguration != null;// && mObsrConfiguration.creator != null;
    }

    @Override
    public boolean isHfcsEnabled() {
        return mHfcsConfiguration != null;// && mHfcsConfiguration.creator != null;
    }
}
