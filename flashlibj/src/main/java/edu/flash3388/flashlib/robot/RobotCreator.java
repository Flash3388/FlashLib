package edu.flash3388.flashlib.robot;

@FunctionalInterface
public interface RobotCreator {

    Robot create() throws RobotCreationException;
}
