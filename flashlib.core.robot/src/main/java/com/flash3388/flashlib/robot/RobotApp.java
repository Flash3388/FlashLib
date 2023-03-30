package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.FlashLibApp;
import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class RobotApp implements FlashLibApp {

    private static final Logger LOGGER = Logging.getMainLogger();
    
    private final RobotBase mRobotBase;
    private final RobotControl mControl;

    public RobotApp(RobotBase robotBase, RobotControl control) {
        mRobotBase = robotBase;
        mControl = control;
    }

    @Override
    public void initialize(FlashLibControl control) throws StartupException {
        RunningRobot.setControlInstance(mControl);

        LOGGER.debug("Initializing user robot");
        mRobotBase.robotInit(mControl);
    }

    @Override
    public void main(FlashLibControl control) throws Exception {
        LOGGER.debug("Starting user robot");
        mRobotBase.robotMain();
    }

    @Override
    public void shutdown(FlashLibControl control) throws Exception {
        LOGGER.debug("Shutting down user robot");
        mRobotBase.robotShutdown();
    }
}
