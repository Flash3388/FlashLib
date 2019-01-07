package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.resources.ResourceHolder;

import java.util.logging.Logger;

@FunctionalInterface
public interface RobotCreator {

    RobotBase create(Logger logger, ResourceHolder resourceHolder) throws RobotCreationException;
}
