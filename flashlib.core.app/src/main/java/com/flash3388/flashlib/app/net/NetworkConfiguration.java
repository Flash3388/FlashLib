package com.flash3388.flashlib.app.net;

public class NetworkConfiguration implements NetworkingMode {

    final boolean mEnabled;
    final ObsrConfiguration mObsrConfiguration;
    final Object mHfcsConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 ObsrConfiguration obsrConfiguration,
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

    public static NetworkConfiguration enabled(ObsrConfiguration objectStorageConfiguration,
                                               Object hfcsConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration, hfcsConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObsrConfiguration != null && mObsrConfiguration.isEnabled;
    }

    @Override
    public boolean isHfcsEnabled() {
        return mHfcsConfiguration != null;// && mHfcsConfiguration.creator != null;
    }
}
