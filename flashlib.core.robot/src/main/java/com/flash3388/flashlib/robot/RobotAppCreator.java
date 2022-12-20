package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.AppCreator;
import com.flash3388.flashlib.app.AppImplementation;
import com.flash3388.flashlib.app.FlashLibApp;
import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

public class RobotAppCreator implements AppCreator {

    private final RobotCreator mRobotCreator;

    public RobotAppCreator(RobotCreator robotCreator) {
        mRobotCreator = robotCreator;
    }

    @Override
    public AppImplementation create(InstanceId instanceId, ResourceHolder holder, Logger logger) throws StartupException {
        RobotImplementation implementation = mRobotCreator.create(instanceId, holder, logger);
        FlashLibApp app = new RobotApp(implementation.getRobotBase(), implementation.getRobotControl());
        return new AppImplementation(implementation.getRobotControl(), app);
    }
}
