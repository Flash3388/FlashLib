package com.flash3388.flashlib.app;

public class AppImplementation {

    private final FlashLibControl mControl;
    private final FlashLibApp mApp;

    public AppImplementation(FlashLibControl control, FlashLibApp app) {
        mControl = control;
        mApp = app;
    }

    public FlashLibControl getControl() {
        return mControl;
    }

    public FlashLibApp getApp() {
        return mApp;
    }
}
