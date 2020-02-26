package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.Resource;
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
    public void registerResources(Collection<? extends Resource> resources) {

    }
}
