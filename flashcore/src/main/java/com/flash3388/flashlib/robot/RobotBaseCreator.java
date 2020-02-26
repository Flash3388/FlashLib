package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

@FunctionalInterface
public interface RobotBaseCreator {

    RobotBase create(Logger logger) throws RobotCreationException;
}
