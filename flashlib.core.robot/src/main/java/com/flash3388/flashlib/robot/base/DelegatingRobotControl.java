package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Supplier;

public class DelegatingRobotControl implements RobotControl {

    private final RobotControl mRobotControl;

    protected DelegatingRobotControl(RobotControl robotControl) {
        mRobotControl = robotControl;
    }

    @Override
    public Supplier<? extends RobotMode> getModeSupplier() {
        return mRobotControl.getModeSupplier();
    }

    @Override
    public IoInterface getIoInterface() {
        return mRobotControl.getIoInterface();
    }

    @Override
    public HidInterface getHidInterface() {
        return mRobotControl.getHidInterface();
    }

    @Override
    public Scheduler getScheduler() {
        return mRobotControl.getScheduler();
    }

    @Override
    public Clock getClock() {
        return mRobotControl.getClock();
    }

    @Override
    public Logger getLogger() {
        return mRobotControl.getLogger();
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        mRobotControl.registerCloseables(closeables);
    }
}