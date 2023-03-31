package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public interface ActionControl {

    ActionConfiguration getConfiguration();
    Time getRunTime();
    Time getTimeLeft();

    ExecutionContext newExecutionContext(ActionInterface actionInterface, ActionConfiguration configuration);

    void finish();
    void cancel();
}
