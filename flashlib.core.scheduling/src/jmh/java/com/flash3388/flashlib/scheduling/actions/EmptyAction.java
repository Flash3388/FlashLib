package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Time;

import java.util.Collections;

public class EmptyAction extends ActionBase {

    public EmptyAction(Scheduler scheduler, Time timeout) {
        super(scheduler, new ActionConfiguration(Collections.emptyList(), timeout, "",
                ActionFlag.RUN_ON_DISABLED));
    }

    public EmptyAction(Scheduler scheduler) {
        this(scheduler, Time.INVALID);
    }

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {

    }

    @Override
    public void end(FinishReason reason) {

    }
}
