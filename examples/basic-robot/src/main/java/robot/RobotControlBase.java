package robot;

import com.flash3388.flashlib.robot.IntervalLoopingRobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class RobotControlBase extends IntervalLoopingRobotControl {

    private final Logger mLogger;
    private final Supplier<? extends RobotMode> mRobotModeSupplier;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;

    RobotControlBase(Logger logger) {
        super(UserRobot::new);

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
    public final Supplier<? extends RobotMode> getModeSupplier() {
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
