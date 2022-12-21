package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.flash3388.flashlib.util.unique.InstanceIdGenerator;
import org.slf4j.Logger;

public class FlashLibProgram {

    private final AppCreator mCreator;
    private final Logger mLogger;
    private final InstanceId mInstanceId;

    public FlashLibProgram(AppCreator creator, Logger logger) {
        mCreator = creator;
        mLogger = logger;
        mInstanceId = InstanceIdGenerator.generate();
    }

    public void start() {
        try {
            mLogger.info("Running application id={}", mInstanceId);
            runApplication();
        } catch (StartupException e) {
            mLogger.error("Error while creating application", e);
        } catch (Throwable t) {
            mLogger.error("Unknown error from application", t);
        }

        mLogger.info("Application finished");
    }

    private void runApplication() throws Exception {
        ResourceHolder resourceHolder = ResourceHolder.empty();
        try {
            mLogger.debug("Creating user app class");
            AppImplementation app = mCreator.create(mInstanceId, resourceHolder, mLogger);
            FlashLibInstance.setControl(app.getControl());

            mLogger.debug("Running application");
            runApplication(app);
        } finally {
            try {
                resourceHolder.freeAll();
            }  catch (Throwable t) {
                mLogger.warn("Resource close error", t);
            }
        }
    }

    private void runApplication(AppImplementation appImplementation) throws Exception {
        FlashLibApp app = appImplementation.getApp();
        FlashLibControl control = appImplementation.getControl();

        try {
            appInitialize(app, control);
            appMain(app, control);
        } finally {
            appShutdown(app, control);
        }
    }

    private void appInitialize(FlashLibApp app, FlashLibControl control) throws Exception {
        mLogger.debug("Initializing user app");
        try {
            ServiceRegistry serviceRegistry = control.getServiceRegistry();
            serviceRegistry.startAll();

            app.initialize(control);
        } catch (Throwable t) {
            mLogger.error("Error in app initialization", t);
            throw t;
        }
    }

    private void appMain(FlashLibApp app, FlashLibControl control) throws Exception {
        mLogger.debug("Starting user app");
        try {
            app.main(control);
        } catch (Throwable t) {
            mLogger.error("Error in app main", t);
            throw t;
        }
    }

    private void appShutdown(FlashLibApp app, FlashLibControl control) {
        mLogger.debug("Shutting down user app");
        try {
            app.shutdown(control);
        } catch (Throwable t) {
            mLogger.error("Error in app shutdown", t);
        }

        try {
            ServiceRegistry serviceRegistry = control.getServiceRegistry();
            serviceRegistry.stopAll();
        } catch (Throwable t) {
            mLogger.error("Error in app shutdown", t);
        }
    }
}
