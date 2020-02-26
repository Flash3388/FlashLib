package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

public class RobotProgram {

    private final RobotControlCreator mRobotControlCreator;
    private final Logger mLogger;

    public RobotProgram(RobotControlCreator robotControlCreator, Logger logger) {
        mRobotControlCreator = robotControlCreator;
        mLogger = logger;
    }

    public void start() {
        mLogger.info("Initializing robot");
        initializeRobot();

        try {
            mLogger.info("Running robot");
            runRobot();
        } catch (RobotCreationException e) {
            mLogger.error("Error while creating robot", e);
        } catch (RobotInitializationException e) {
            mLogger.error("Error while initializing robot", e);
        } catch (Throwable t) {
            mLogger.error("Unknown error from robot", t);
        }

        mLogger.info("Robot finished");
    }

    private void initializeRobot() {
        //setting the JVM thread priority for this thread. Should be highest possible.
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    private void runRobot() throws RobotInitializationException, RobotCreationException {
        mLogger.debug("Creating user robot class");

        RobotControlBase robot = mRobotControlCreator.create(mLogger);

        RunningRobot.setInstance(robot);

        mLogger.debug("Initializing user robot");
        try {
            runRobot(robot);
        } finally {
            robot.getResourceHolder().freeAll();
        }
    }

    private void runRobot(RobotControlBase robot) throws RobotInitializationException {
        robot.robotInit();
        try {
            mLogger.debug("Starting user robot");
            robot.robotMain();
        } finally {
            mLogger.debug("Shutting down user robot");
            robot.robotShutdown();
        }
    }
}