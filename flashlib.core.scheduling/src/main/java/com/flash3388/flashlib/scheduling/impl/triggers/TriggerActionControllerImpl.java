package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class TriggerActionControllerImpl implements TriggerActionController {

    private final List<Action> mActionsToStartIfRunning;
    private final List<Action> mActionsToStopIfRunning;
    private final List<Action> mActionsToToggle;

    public TriggerActionControllerImpl() {
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

    @Override
    public void addActionToStartIfRunning(Action action) {
        mActionsToStartIfRunning.add(action);
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
