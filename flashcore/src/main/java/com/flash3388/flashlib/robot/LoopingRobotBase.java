package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.modes.RobotMode;

public abstract class LoopingRobotBase extends RobotBase {

    private final IterativeRobot.Initializer mRobotInitializer;

    private IterativeRobot mRobot;
    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    protected LoopingRobotBase(IterativeRobot.Initializer robotInitializer) {
        mRobotInitializer = robotInitializer;

        mCurrentMode = null;
        mLastMode = null;
        mWasCurrentModeInitialized = false;
    }

    @Override
    protected final void robotInit() throws RobotInitializationException {
        mRobot = mRobotInitializer.init(this);
    }

    @Override
    protected final void robotShutdown(){
        stopRobotLoop();

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

    protected abstract void stopRobotLoop();
}
