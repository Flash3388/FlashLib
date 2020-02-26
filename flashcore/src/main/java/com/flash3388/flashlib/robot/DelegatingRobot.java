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

public class DelegatingRobot implements Robot {

    private final Robot mRobot;

    protected DelegatingRobot(Robot robot) {
        mRobot = robot;
    }

    @Override
    public Supplier<? extends RobotMode> getModeSupplier() {
        return mRobot.getModeSupplier();
    }

    @Override
    public IoInterface getIoInterface() {
        return mRobot.getIoInterface();
    }

    @Override
    public HidInterface getHidInterface() {
        return mRobot.getHidInterface();
    }

    @Override
    public Scheduler getScheduler() {
        return mRobot.getScheduler();
    }

    @Override
    public Clock getClock() {
        return mRobot.getClock();
    }

    @Override
    public Logger getLogger() {
        return mRobot.getLogger();
    }

    @Override
    public void registerResources(Collection<? extends Resource> resources) {

    }
}
