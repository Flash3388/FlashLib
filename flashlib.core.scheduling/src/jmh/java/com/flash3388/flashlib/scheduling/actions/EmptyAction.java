package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Time;

import java.util.Collections;

public class EmptyAction extends ActionBase {

    public EmptyAction(Scheduler scheduler, Time timeout) {
        super(scheduler, new ActionConfiguration(Collections.emptyList(), timeout, "", true));
    }

    public EmptyAction(Scheduler scheduler) {
        this(scheduler, Time.INVALID);
    }

    @Override
    public void execute() {

    }
}
