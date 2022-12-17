package com.flash3388.flashlib.robot.net;

public class NetworkConfiguration implements NetworkingMode {

    private final boolean mEnabled;

    private NetworkConfiguration(boolean enabled) {
        mEnabled = enabled;
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration(false);
    }


    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }
}
