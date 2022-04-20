package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.modes.RobotMode;

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
    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    /**
     * Creates a new looping base.
     *
     * @param robotInitializer initializer for user robot logic.
     * @param robotLooper looper object to run the loops periodically.
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
    public final void robotInit(RobotControl robotControl) throws RobotInitializationException {
        mRobotControl = robotControl;
        mRobot = mRobotInitializer.init(robotControl);
    }

    @Override
    public final void robotMain() {
        mRobotLooper.startLooping(mRobotControl.getClock(), this::robotLoop);
    }

    @Override
    public final void robotShutdown(){
        mRobotLooper.stop();

        mRobotControl.getScheduler().cancelAllActions();
        mRobot.robotStop();
    }

    protected final void robotLoop(){
        mCurrentMode = mRobotControl.getMode();

        if (!mCurrentMode.equals(mLastMode)) {
            exitMode(mCurrentMode);
            mLastMode = mCurrentMode;
            mWasCurrentModeInitialized = false;
        }

        if (!mWasCurrentModeInitialized) {
            initMode(mCurrentMode);
            mWasCurrentModeInitialized = true;
        }

        periodicMode(mCurrentMode);
    }

    private void initMode(RobotMode mode) {
        mRobotControl.getLogger().trace("Initializing mode {}", mode);

        if (mode.isDisabled()) {
            mRobotControl.getScheduler().cancelActionsIf((a)->!a.getConfiguration().shouldRunWhenDisabled());
            mRobot.disabledInit();
        } else {
            mRobot.modeInit(mode);
        }
    }

    private void periodicMode(RobotMode mode) {
        mRobotControl.getLogger().trace("Periodic mode {}", mode);

        mRobotControl.getScheduler().run(mode);
        if (mode.isDisabled()) {
            mRobot.disabledPeriodic();
        } else {
            mRobot.modePeriodic(mode);
        }

        mRobotControl.getLogger().trace("Robot periodic");

        mRobot.robotPeriodic();
    }

    private void exitMode(RobotMode mode) {
        mRobotControl.getLogger().trace("Exiting mode {}", mode);

        if (mode.isDisabled()) {
            mRobot.disabledExit();
        } else {
            mRobot.modeExit(mode);
        }
    }
}
