package edu.flash3388.flashlib.robot;

@FunctionalInterface
public interface RobotCreator {

    RobotBase create() throws RobotCreationException;
}
