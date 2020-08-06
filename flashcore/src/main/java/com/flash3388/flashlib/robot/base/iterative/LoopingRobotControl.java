package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.robot.BaseRobot;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.modes.RobotMode;

public abstract class LoopingRobotControl implements BaseRobot {

    private final IterativeRobot.Initializer mRobotInitializer;
    private final RobotLooper mRobotLooper;

    private IterativeRobot mRobot;
    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    protected LoopingRobotControl(IterativeRobot.Initializer robotInitializer, RobotLooper robotLooper) {
        mRobotInitializer = robotInitializer;
        mRobotLooper = robotLooper;

        mCurrentMode = null;
        mLastMode = null;
        mWasCurrentModeInitialized = false;
    }

    protected LoopingRobotControl(IterativeRobot.Initializer robotInitializer) {
        this(robotInitializer, new RobotIntervalLooper());
    }

    @Override
    public final void robotInit() throws RobotInitializationException {
        mRobot = mRobotInitializer.init(this);
    }

    @Override
    public final void robotMain() {
        mRobotLooper.doLoop(getClock(), this::robotLoop);
    }

    @Override
    public final void robotShutdown(){
        mRobotLooper.stopLoop();

        getScheduler().cancelAllActions();
        mRobot.robotStop();
    }

    protected final void robotLoop(){
        mCurrentMode = getMode();

        if (!mCurrentMode.equals(mLastMode)) {
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
        getLogger().trace("Initializing mode {}", mode);

        if (mode.isDisabled()) {
            getScheduler().cancelActionsIf((a)->!a.getConfiguration().shouldRunWhenDisabled());
            mRobot.disabledInit();
        } else {
            mRobot.modeInit(mode);
        }
    }

    private void periodicMode(RobotMode mode) {
        getLogger().trace("Periodic mode {}", mode);

        getScheduler().run(mode);
        if (mode.isDisabled()) {
            mRobot.disabledPeriodic();
        } else {
            mRobot.modePeriodic(mode);
        }

        getLogger().trace("Robot periodic");

        mRobot.robotPeriodic();
    }
}
