package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class FlashLibMain {

    private FlashLibMain() {}

    public static void appMain(AppCreator creator, Logger logger) {
        FlashLibProgram program = new FlashLibProgram(creator, logger);
        program.start();
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier, Logger logger) {
        AppCreator creator = new BasicCreator(appSupplier);
        appMain(creator, logger);
    }

    public static void appMain(SimpleApp.Creator creator, Logger logger) {
        appMain(()-> new SimpleAppRunner(creator), logger);
    }

    private static class BasicCreator implements AppCreator {

        private final Supplier<FlashLibApp> mAppSupplier;

        private BasicCreator(Supplier<FlashLibApp> appSupplier) {
            mAppSupplier = appSupplier;
        }

        @Override
        public AppImplementation create(InstanceId instanceId, ResourceHolder holder, Logger logger) throws StartupException {
            FlashLibControl control = new BasicFlashLibControl(instanceId, holder, logger);
            FlashLibApp app = mAppSupplier.get();
            return new AppImplementation(control, app);
        }
    }

}