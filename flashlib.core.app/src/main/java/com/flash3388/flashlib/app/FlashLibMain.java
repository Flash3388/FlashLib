package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.function.Supplier;

public class FlashLibMain {

    private FlashLibMain() {}

    public static void appMain(AppCreator creator) {
        FlashLibProgram program = new FlashLibProgram(creator);
        program.start();
    }

    public static void appMain(AppCreator creator, InstanceId instanceId) {
        FlashLibProgram program = new FlashLibProgram(creator, instanceId);
        program.start();
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier, InstanceId instanceId) {
        AppCreator creator = new BasicCreator(appSupplier);
        appMain(creator, instanceId);
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier) {
        AppCreator creator = new BasicCreator(appSupplier);
        appMain(creator);
    }

    public static void appMain(SimpleApp.Creator creator, InstanceId instanceId) {
        appMain(()-> new SimpleAppRunner(creator), instanceId);
    }

    public static void appMain(SimpleApp.Creator creator) {
        appMain(()-> new SimpleAppRunner(creator));
    }

    private static class BasicCreator implements AppCreator {

        private final Supplier<FlashLibApp> mAppSupplier;

        private BasicCreator(Supplier<FlashLibApp> appSupplier) {
            mAppSupplier = appSupplier;
        }

        @Override
        public AppImplementation create(InstanceId instanceId, ResourceHolder holder) throws StartupException {
            FlashLibControl control = new BasicFlashLibControl(instanceId, holder);
            FlashLibApp app = mAppSupplier.get();
            return new AppImplementation(control, app);
        }
    }

}
