package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

public class RobotProgram {

    private final RobotCreator mRobotCreator;
    private final Logger mLogger;

    public RobotProgram(RobotCreator robotCreator, Logger logger) {
        mRobotCreator = robotCreator;
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
        ResourceHolder resourceHolder = ResourceHolder.empty();
        try {
            mLogger.debug("Creating user robot class");
            BaseRobot robot = mRobotCreator.create(mLogger, resourceHolder);
            RunningRobot.setInstance(robot);

            mLogger.debug("Initializing user robot");
            runRobot(robot);
        } finally {
            try {
                resourceHolder.freeAll();
            }  catch (Throwable t) {
                mLogger.warn("Resource close error", t);
            }
        }
    }

    private void runRobot(BaseRobot robot) throws RobotInitializationException {
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
