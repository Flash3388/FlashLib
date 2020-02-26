package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

@FunctionalInterface
public interface RobotControlCreator {

    RobotControlBase create(Logger logger) throws RobotCreationException;
}
