package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Time;

public interface ActionContext {

    Action getUnderlyingAction();
    Time getRunTime();

    boolean startRun();
    boolean run(SchedulerMode mode);

    void cancelAction();
}
