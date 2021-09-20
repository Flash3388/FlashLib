package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

@FunctionalInterface
public interface RobotCreator {

    RobotImplementation create(Managers managers, Logger logger) throws RobotCreationException;
}
