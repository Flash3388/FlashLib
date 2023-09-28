package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class TriggerActionController {

    private final List<Action> mActionsToStartIfRunning;
    private final List<Action> mActionsToStopIfRunning;
    private final List<Action> mActionsToToggle;

    public TriggerActionController() {
        mActionsToStartIfRunning = new ArrayList<>(2);
        mActionsToStopIfRunning = new ArrayList<>(2);
        mActionsToToggle = new ArrayList<>(2);
    }

    public List<Action> getActionsToStartIfRunning() {
        return mActionsToStartIfRunning;
    }

    public List<Action> getActionsToStopIfRunning() {
        return mActionsToStopIfRunning;
    }

    public List<Action> getActionsToToggle() {
        return mActionsToToggle;
    }

    public void addActionToStartIfRunning(Action action) {
        mActionsToStartIfRunning.add(action);
    }

    public void addActionToStopIfRunning(Action action) {
        mActionsToStopIfRunning.add(action);
    }

    public void addActionToToggle(Action action) {
        mActionsToToggle.add(action);
    }
}
