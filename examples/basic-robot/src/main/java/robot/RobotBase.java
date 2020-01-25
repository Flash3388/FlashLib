package robot;

import com.flash3388.flashlib.robot.IterativeRobot;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public abstract class RobotBase extends IterativeRobot {

    private final Logger mLogger;
    private final RobotModeSupplier mRobotModeSupplier;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;

    protected RobotBase(Logger logger) {
        mLogger = logger;
        mRobotModeSupplier = new StaticRobotModeSupplier(RobotMode.DISABLED);
        mIoInterface = new IoInterface.Stub();
        mHidInterface = new HidInterface.Stub();
        mClock = RobotFactory.newDefaultClock();
        mScheduler = RobotFactory.newDefaultScheduler(mClock, mLogger);
    }

    @Override
    public final Logger getLogger() {
        return mLogger;
    }

    @Override
    public final RobotModeSupplier getModeSupplier() {
        return mRobotModeSupplier;
    }

    @Override
    public final IoInterface getIoInterface() {
        return mIoInterface;
    }

    @Override
    public final HidInterface getHidInterface() {
        return mHidInterface;
    }

    @Override
    public final Scheduler getScheduler() {
        return mScheduler;
    }

    @Override
    public final Clock getClock() {
        return mClock;
    }
}
