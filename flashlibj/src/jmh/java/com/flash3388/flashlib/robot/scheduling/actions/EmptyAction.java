package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Time;

public class EmptyAction extends Action {

    public EmptyAction(Scheduler scheduler, Time timeout) {
        super(scheduler, timeout);
    }

    public EmptyAction(Scheduler scheduler) {
        this(scheduler, Time.INVALID);
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void end() {

    }
}
