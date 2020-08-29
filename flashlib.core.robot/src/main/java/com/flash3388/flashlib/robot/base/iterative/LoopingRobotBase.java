package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.modes.RobotMode;

public class LoopingRobotBase implements RobotBase {

    private final IterativeRobot.Initializer mRobotInitializer;
    private final RobotLooper mRobotLooper;

    private RobotControl mRobotControl;
    private IterativeRobot mRobot;
    private RobotMode mCurrentMode;
    private RobotMode mLastMode;
    private boolean mWasCurrentModeInitialized;

    public LoopingRobotBase(IterativeRobot.Initializer robotInitializer, RobotLooper robotLooper) {
        mRobotInitializer = robotInitializer;
        mRobotLooper = robotLooper;

        mRobotControl = null;
        mCurrentMode = null;
        mLastMode = null;
        mWasCurrentModeInitialized = false;
    }

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
}
