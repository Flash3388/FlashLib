package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;

public interface TriggerActionController {

    void addActionToStartIfNotRunning(Action action);
    void addActionToStopIfRunning(Action action);
    void addActionToToggle(Action action);
}
