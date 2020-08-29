package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

@FunctionalInterface
public interface RobotCreator {

    RobotImplementation create(Logger logger, ResourceHolder resourceHolder) throws RobotCreationException;
}
