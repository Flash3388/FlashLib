package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.FlashLibApp;
import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.robot.base.RobotBase;
import org.slf4j.Logger;

public class RobotApp implements FlashLibApp {

    private final RobotBase mRobotBase;
    private final RobotControl mControl;
    private final Logger mLogger;

    public RobotApp(RobotBase robotBase, RobotControl control) {
        mRobotBase = robotBase;
        mControl = control;
        mLogger = control.getLogger();
    }

    @Override
    public void initialize(FlashLibControl control) throws StartupException {
        RunningRobot.setControlInstance(mControl);

        mLogger.debug("Initializing user robot");
        mRobotBase.robotInit(mControl);
    }

    @Override
    public void main(FlashLibControl control) throws Exception {
        mLogger.debug("Starting user robot");
        mRobotBase.robotMain();
    }

    @Override
    public void shutdown(FlashLibControl control) throws Exception {
        mLogger.debug("Shutting down user robot");
        mRobotBase.robotShutdown();
    }
}
