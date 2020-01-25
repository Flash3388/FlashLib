package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

@FunctionalInterface
public interface RobotCreator {

    RobotBase create(Logger logger) throws RobotCreationException;
}
