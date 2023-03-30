package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.flash3388.flashlib.util.unique.InstanceIdGenerator;
import org.slf4j.Logger;

public class FlashLibProgram {

    private static final Logger LOGGER = Logging.getMainLogger();
    
    private final AppCreator mCreator;
    private final InstanceId mInstanceId;

    public FlashLibProgram(AppCreator creator, InstanceId instanceId) {
        mCreator = creator;
        mInstanceId = instanceId;
    }

    public FlashLibProgram(AppCreator creator) {
        this(creator, InstanceIdGenerator.generate());
    }

    public void start() {
        try {
            LOGGER.info("Running application id={}", mInstanceId);
            runApplication();
        } catch (StartupException e) {
            LOGGER.error("Error while creating application", e);
        } catch (Throwable t) {
            LOGGER.error("Unknown error from application", t);
        }

        LOGGER.info("Application finished");
    }

    private void runApplication() throws Exception {
        ResourceHolder resourceHolder = ResourceHolder.empty();
        try {
            LOGGER.debug("Creating user app class");
            AppImplementation app = mCreator.create(mInstanceId, resourceHolder);
            FlashLibInstance.setControl(app.getControl());

            LOGGER.debug("Running application");
            runApplication(app);
        } finally {
            try {
                resourceHolder.freeAll();
            }  catch (Throwable t) {
                LOGGER.warn("Resource close error", t);
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
        LOGGER.debug("Initializing user app");
        try {
            ServiceRegistry serviceRegistry = control.getServiceRegistry();
            serviceRegistry.startAll();

            app.initialize(control);
        } catch (Throwable t) {
            LOGGER.error("Error in app initialization", t);
            throw t;
        }
    }

    private void appMain(FlashLibApp app, FlashLibControl control) throws Exception {
        LOGGER.debug("Starting user app");
        try {
            app.main(control);
        } catch (Throwable t) {
            LOGGER.error("Error in app main", t);
            throw t;
        }
    }

    private void appShutdown(FlashLibApp app, FlashLibControl control) {
        LOGGER.debug("Shutting down user app");
        try {
            app.shutdown(control);
        } catch (Throwable t) {
            LOGGER.error("Error in app shutdown", t);
        }

        try {
            ServiceRegistry serviceRegistry = control.getServiceRegistry();
            serviceRegistry.stopAll();
        } catch (Throwable t) {
            LOGGER.error("Error in app shutdown", t);
        }
    }
}
