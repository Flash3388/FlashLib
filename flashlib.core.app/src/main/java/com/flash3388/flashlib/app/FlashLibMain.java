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

    public static void appMain(Supplier<FlashLibApp> appSupplier, ControlCreator controlCreator, InstanceId instanceId) {
        AppCreator creator = new BasicAppCreator(appSupplier, controlCreator);
        appMain(creator, instanceId);
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier, ControlCreator controlCreator) {
        AppCreator creator = new BasicAppCreator(appSupplier, controlCreator);
        appMain(creator);
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier, InstanceId instanceId) {
        appMain(appSupplier, new BasicControlCreator(), instanceId);
    }

    public static void appMain(Supplier<FlashLibApp> appSupplier) {
        AppCreator creator = new BasicAppCreator(appSupplier, new BasicControlCreator());
        appMain(creator);
    }

    public static void appMain(SimpleApp.Creator creator, ControlCreator controlCreator, InstanceId instanceId) {
        appMain(()-> new SimpleAppRunner(creator), controlCreator, instanceId);
    }

    public static void appMain(SimpleApp.Creator creator, ControlCreator controlCreator) {
        appMain(()-> new SimpleAppRunner(creator), controlCreator);
    }

    public static void appMain(SimpleApp.Creator creator, InstanceId instanceId) {
        appMain(()-> new SimpleAppRunner(creator), instanceId);
    }

    public static void appMain(SimpleApp.Creator creator) {
        appMain(()-> new SimpleAppRunner(creator));
    }

    private static class BasicAppCreator implements AppCreator {

        private final Supplier<FlashLibApp> mAppSupplier;
        private final ControlCreator mControlCreator;

        private BasicAppCreator(Supplier<FlashLibApp> appSupplier, ControlCreator controlCreator) {
            mAppSupplier = appSupplier;
            mControlCreator = controlCreator;
        }

        @Override
        public AppImplementation create(InstanceId instanceId, ResourceHolder holder) throws StartupException {
            FlashLibControl control = mControlCreator.create(instanceId, holder);
            FlashLibApp app = mAppSupplier.get();
            return new AppImplementation(control, app);
        }
    }

    private static class BasicControlCreator implements ControlCreator {

        @Override
        public FlashLibControl create(InstanceId instanceId, ResourceHolder holder) throws StartupException {
            return new BasicFlashLibControl(instanceId, holder);
        }
    }
}
