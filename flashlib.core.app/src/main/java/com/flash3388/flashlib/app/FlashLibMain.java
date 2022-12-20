package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

public class FlashLibMain {

    private FlashLibMain() {}

    public static void appMain(AppCreator creator, Logger logger) {
        FlashLibProgram program = new FlashLibProgram(creator, logger);
        program.start();
    }

    public static void appMain(FlashLibApp app, Logger logger) {
        AppCreator creator = new BasicCreator(app);
        appMain(creator, logger);
    }

    private static class BasicCreator implements AppCreator {

        private final FlashLibApp mApp;

        private BasicCreator(FlashLibApp app) {
            mApp = app;
        }

        @Override
        public AppImplementation create(InstanceId instanceId, ResourceHolder holder, Logger logger) throws StartupException {
            FlashLibControl control = new BasicFlashLibControl(instanceId, holder, logger);
            return new AppImplementation(control, mApp);
        }
    }
}
