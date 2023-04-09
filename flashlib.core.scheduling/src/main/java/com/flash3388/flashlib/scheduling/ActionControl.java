package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

public interface ActionControl {

    ActionConfiguration getConfiguration();
    Time getRunTime();
    Time getTimeLeft();

    void finish();
    void cancel();
}
