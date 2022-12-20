package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

@FunctionalInterface
public interface RobotCreator {

    RobotImplementation create(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger) throws StartupException;
}
