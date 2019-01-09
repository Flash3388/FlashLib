package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.util.resources.Resource;
import edu.flash3388.flashlib.util.resources.ResourceHolder;

import java.util.logging.Logger;

/**
 * <p>
 *     The base class for robot main classes.
 * </p>
 * Inheriting robot classes need to implement the following methods:
 * <ul>
 *     <li>{@link #robotInit()}: called when the robot is initialized.
 *          Should be used to initialize robot components</li>
 *     <li>{@link #robotMain()}: the robot main method. Called after initialization,
 *           and should implement the robot logic.</li>
 *     <li>{@link #robotShutdown()}: called after {@link #robotMain()} is finished. Used
 *           for freeing resources and components initialized in {@link #robotInit()}.</li>
 * </ul>
 * <p>
 *     If {@link #robotMain()} throws an exception, {@link #robotShutdown()} is called.
 * </p>
 *
 * @since FlashLib 1.2.0
 */
public abstract class RobotBase implements Robot {

    private final Clock mClock;
    private final Scheduler mScheduler;
    private final HidInterface mHidInterface;

    private ResourceHolder mResourceHolder;
    private Logger mLogger;

    protected RobotBase(Clock clock, Scheduler scheduler, HidInterface hidInterface) {
        mClock = clock;
        mScheduler = scheduler;
        mHidInterface = hidInterface;
    }

    @Override
    public Clock getClock() {
        return mClock;
    }

    @Override
    public Scheduler getScheduler() {
        return mScheduler;
    }

    @Override
    public HidInterface getHidInterface() {
        return mHidInterface;
    }

    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
    public void registerResources(Resource... resources) {
        for(Resource resource : resources) {
            mResourceHolder.add(resource);
        }
    }

    final void initResources(ResourceHolder resourceHolder, Logger logger) {
        mResourceHolder = resourceHolder;
        mLogger = logger;
    }

    /**
     * Called when robot initialization starts, allowing for initialization of user code.
     *
     * @throws RobotInitializationException if an error occurs while initializing
     */
    protected abstract void robotInit() throws RobotInitializationException;

    /**
     * Called when {@link Robot} finished initialization and the robot can be started.
     * This is the main method of the robot and all operations should be directed from here.
     */
    protected abstract void robotMain();

    /**
     * Called when the robot finishes running, allowing to perform custom stop operations. Should be used
     * to free robot components.
     */
    protected abstract void robotShutdown();
}
