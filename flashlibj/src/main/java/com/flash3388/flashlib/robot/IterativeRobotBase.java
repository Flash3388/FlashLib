package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.modes.RobotMode;

public abstract class IterativeRobotBase extends RobotBase {

    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    protected IterativeRobotBase() {
        mCurrentMode = null;
        mLastMode = null;
        mWasCurrentModeInitialized = false;
    }

    @Override
    protected final void robotShutdown(){
        stopRobotLoop();

        getScheduler().stopAllActions();

        robotStop();
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
            disabledInit();
        } else {
            modeInit(mode);
        }
    }

    private void periodicMode(RobotMode mode) {
        getLogger().trace("Periodic mode {}", mode);

        getScheduler().run(mode);
        if (mode.isDisabled()) {
            disabledPeriodic();
        } else {
            modePeriodic(mode);
        }

        getLogger().trace("Robot periodic");

        robotPeriodic();
    }

    //--------------------------------------------------------------------
    //----------------------Implementable---------------------------------
    //--------------------------------------------------------------------

    protected abstract void stopRobotLoop();

    protected void robotStop(){}

    @Override
    protected abstract void robotInit() throws RobotInitializationException;
    protected abstract void robotPeriodic();

    protected abstract void disabledInit();
    protected abstract void disabledPeriodic();

    protected abstract void modeInit(RobotMode mode);
    protected abstract void modePeriodic(RobotMode mode);
}
