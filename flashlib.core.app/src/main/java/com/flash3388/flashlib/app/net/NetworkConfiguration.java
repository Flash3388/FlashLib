package com.flash3388.flashlib.app.net;

public class NetworkConfiguration implements NetworkingMode {

    private final boolean mEnabled;
    private final ObsrConfiguration mObsrConfiguration;
    private final HfcsConfiguration mHfcsConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 ObsrConfiguration obsrConfiguration,
                                 HfcsConfiguration hfcsConfiguration) {
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
                                               HfcsConfiguration hfcsConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration, hfcsConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObsrConfiguration != null && mObsrConfiguration.creator != null;
    }

    @Override
    public boolean isHfcsEnabled() {
        return mHfcsConfiguration != null && mHfcsConfiguration.creator != null;
    }

    public ObsrConfiguration getObsrConfiguration() {
        return mObsrConfiguration;
    }

    public HfcsConfiguration getHfcsConfiguration() {
        return mHfcsConfiguration;
    }
}
