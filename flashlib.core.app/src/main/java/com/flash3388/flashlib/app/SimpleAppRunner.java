package com.flash3388.flashlib.app;

public class SimpleAppRunner implements FlashLibApp {

    private final SimpleApp.Creator mCreator;
    private SimpleApp mApp;

    public SimpleAppRunner(SimpleApp.Creator creator) {
        mCreator = creator;
        mApp = null;
    }

    @Override
    public void initialize(FlashLibControl control) throws StartupException {
        mApp = mCreator.create(control);
    }

    @Override
    public void main(FlashLibControl control) throws Exception {
        mApp.main();
    }

    @Override
    public void shutdown(FlashLibControl control) throws Exception {
        if (mApp != null) {
            mApp.close();
        }
    }
}
