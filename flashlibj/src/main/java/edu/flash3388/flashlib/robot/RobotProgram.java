package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

public class RobotProgram {

    private final RobotCreator mRobotCreator;
    private final Logger mLogger;
    private final ResourceHolder mResourceHolder;

    public RobotProgram(RobotCreator robotCreator, Logger logger) {
        mRobotCreator = robotCreator;
        mLogger = logger;

        mResourceHolder = ResourceHolder.empty();
    }

    public void start() {
        initializeRobot();

        try {
            runRobot();
        } catch (RobotCreationException e) {
            mLogger.error("Error while creating robot", e);
        } catch (RobotInitializationException e) {
            mLogger.error("Error while initializing robot", e);
        } catch (Throwable t) {
            mLogger.error("Unknown error from robot", t);
        } finally {
            freeRobotResources();
        }
    }

    private void initializeRobot() {
        //setting the JVM thread priority for this thread. Should be highest possible.
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    private void runRobot() throws RobotInitializationException, RobotCreationException {
        RobotBase robot = mRobotCreator.create();
        robot.initResources(mResourceHolder, mLogger);

        RunningRobot.INSTANCE.set(robot);

        robot.robotInit();
        try {
            robot.robotMain();
        } finally {
            robot.robotShutdown();
        }
    }

    private void freeRobotResources() {
        mResourceHolder.freeAll();
    }
}
