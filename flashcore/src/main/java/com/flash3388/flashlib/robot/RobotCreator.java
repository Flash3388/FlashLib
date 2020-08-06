package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

@FunctionalInterface
public interface RobotCreator {

    BaseRobot create(Logger logger, ResourceHolder resourceHolder) throws RobotCreationException;
}
