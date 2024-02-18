package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class TriggerActionControllerImpl implements TriggerActionController {

    private final List<Action> mActionsToStartIfNotRunning;
    private final List<Action> mActionsToStopIfRunning;
    private final List<Action> mActionsToToggle;

    public TriggerActionControllerImpl() {
        mActionsToStartIfNotRunning = new ArrayList<>(2);
        mActionsToStopIfRunning = new ArrayList<>(2);
        mActionsToToggle = new ArrayList<>(2);
    }

    public List<Action> getActionsToStartIfNotRunning() {
        return mActionsToStartIfNotRunning;
    }

    public List<Action> getActionsToStopIfRunning() {
        return mActionsToStopIfRunning;
    }

    public List<Action> getActionsToToggle() {
        return mActionsToToggle;
    }

    public void clear() {
        mActionsToStartIfNotRunning.clear();
        mActionsToStopIfRunning.clear();
        mActionsToToggle.clear();
    }

    @Override
    public void addActionToStartIfNotRunning(Action action) {
        mActionsToStartIfNotRunning.add(action);
    }

    @Override
    public void addActionToStopIfRunning(Action action) {
        mActionsToStopIfRunning.add(action);
    }

    @Override
    public void addActionToToggle(Action action) {
        mActionsToToggle.add(action);
    }
}
