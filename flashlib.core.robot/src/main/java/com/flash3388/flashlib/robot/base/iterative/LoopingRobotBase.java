package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.app.watchdog.Watchdog;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;

/**
 * A {@link RobotBase} implementation for loop-based (iterative) robots.
 * Perfect when using with mode-oriented algorithms.
 *
 * @since FlashLib 2.0.0
 *
 * @see IterativeRobot
 */
public class LoopingRobotBase implements RobotBase {

    private final IterativeRobot.Initializer mRobotInitializer;
    private final RobotLooper mRobotLooper;

    private RobotControl mRobotControl;
    private IterativeRobot mRobot;
    private Watchdog mLoopWatchdog;
    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    /**
     * Creates a new looping base.
     *
     * @param robotInitializer initializer for user robot logic.
     * @param robotLooper      looper object to run the loops periodically.
     */
    public LoopingRobotBase(IterativeRobot.Initializer robotInitializer, RobotLooper robotLooper) {
        mRobotInitializer = robotInitializer;
        mRobotLooper = robotLooper;

        mRobotControl = null;
        mCurrentMode = null;
        mLastMode = null;
        mWasCurrentModeInitialized = false;
    }

    /**
     * Creates a new looping base.
     * <p>
     *     Loops at a period of <em>20ms</em> using {@link Thread#sleep(long)} to
     *     wait the time between each runs.
     * </p>
     *
     * @param robotInitializer initializer for user robot logic.
     *
     * @see RobotIntervalLooper
     */
    public LoopingRobotBase(IterativeRobot.Initializer robotInitializer) {
        this(robotInitializer, new RobotIntervalLooper());
    }

    @Override
    public final void robotInit(RobotControl robotControl) throws StartupException {
        mRobotControl = robotControl;
        mLoopWatchdog = robotControl.newWatchdog("LoopingRobotBase", mRobotLooper.getLoopRunPeriod());

        mRobot = mRobotInitializer.init(robotControl);
    }

    @Override
    public final void robotMain() {
        mRobotLooper.startLooping(mRobotControl.getClock(), this::robotLoop);
    }

    @Override
    public final void robotShutdown(){
        mRobotLooper.stop();

        if (mRobotControl != null) {
            mRobotControl.getScheduler().cancelAllActions();
        }
        if (mRobot != null) {
            mRobot.robotStop();
        }
    }

    protected final void robotLoop(){
        mLoopWatchdog.enable();

        mCurrentMode = mRobotControl.getMode();

        if (!mCurrentMode.equals(mLastMode) && mWasCurrentModeInitialized) {
            exitMode(mCurrentMode);
            mLastMode = mCurrentMode;
            mWasCurrentModeInitialized = false;
        }

        if (!mWasCurrentModeInitialized) {
            initMode(mCurrentMode);
            mWasCurrentModeInitialized = true;
        }

        periodicMode(mCurrentMode);

        mLoopWatchdog.feed();
        mLoopWatchdog.disable();
    }

    private void initMode(RobotMode mode) {
        mRobotControl.getLogger().debug("Initializing mode {}", mode);

        if (mode.isDisabled()) {
            mRobotControl.getScheduler().cancelActionsIfWithoutFlag(ActionFlag.RUN_ON_DISABLED);
            mRobot.disabledInit();
        } else {
            mRobot.modeInit(mode);
        }
    }

    private void periodicMode(RobotMode mode) {
        mRobotControl.getLogger().debug("Periodic mode {}", mode);

        mRobotControl.getMainThread().executePendingTasks();
        mLoopWatchdog.reportTimestamp("MainThread.executePendingTasks");

        mRobotControl.getServiceRegistry().startAll();
        mLoopWatchdog.reportTimestamp("ServiceRegistry.startAll");

        mRobotControl.getScheduler().run(mode);
        mLoopWatchdog.reportTimestamp("Scheduler.run");

        if (mode.isDisabled()) {
            mRobot.disabledPeriodic();
        } else {
            mRobot.modePeriodic(mode);
        }
        mLoopWatchdog.reportTimestamp("ModePeriodic");

        mRobotControl.getLogger().debug("Robot periodic");
        mRobot.robotPeriodic();
        mLoopWatchdog.reportTimestamp("robotPeriodic");
    }

    private void exitMode(RobotMode mode) {
        mRobotControl.getLogger().debug("Exiting mode {}", mode);

        if (mode.isDisabled()) {
            mRobot.disabledExit();
        } else {
            mRobot.modeExit(mode);
        }
    }
}
