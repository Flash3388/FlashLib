package com.flash3388.flashlib.robot.scheduling.actions.nactions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;

public abstract class ActionBase implements ActionInterface {

    private final Scheduler mScheduler;

    protected ActionBase(Scheduler scheduler) {
        mScheduler = scheduler;
    }
    protected ActionBase() {
        this(RunningRobot.INSTANCE.get().getScheduler());
    }

    @Override
    public void start() {
        //mScheduler.add(this);
    }

    @Override
    public void cancel() {
        //mScheduler.cancel();
    }
}
