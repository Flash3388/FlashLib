package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.RunOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.StartOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.ToggleOnState;

import java.util.ArrayList;
import java.util.Collection;

public class Trigger {

    private final Collection<TriggerStateHandler> mTriggerStateHandlers;
    private TriggerState mCurrentState;

    public Trigger(Collection<TriggerStateHandler> triggerStateHandlers, TriggerState initialState) {
        mTriggerStateHandlers = triggerStateHandlers;
        mCurrentState = initialState;
    }

    public Trigger(TriggerState initialState) {
        this(new ArrayList<>(), initialState);
    }

    public Trigger() {
        this(TriggerState.INACTIVE);
    }

    public Trigger addStateHandler(TriggerStateHandler handler) {
        mTriggerStateHandlers.add(handler);

        return this;
    }

    public Trigger whenActive(Action action) {
        return addStateHandler(new StartOnState(TriggerState.ACTIVE, action));
    }

    public Trigger cancelWhenActive(Action action) {
        return addStateHandler(new CancelOnState(TriggerState.ACTIVE, action));
    }

    public Trigger toggleWhenActive(Action action) {
        return addStateHandler(new ToggleOnState(TriggerState.ACTIVE, action));
    }

    public Trigger whileActive(Action action) {
        return addStateHandler(new RunOnState(TriggerState.ACTIVE, action));
    }

    public Trigger whenInactive(Action action) {
        return addStateHandler(new StartOnState(TriggerState.INACTIVE, action));
    }

    public Trigger cancelWhenInactive(Action action) {
        return addStateHandler(new CancelOnState(TriggerState.INACTIVE, action));
    }

    public void setState(TriggerState newState) {
        if (mCurrentState == newState) {
            return;
        }

        handleStateChange(newState, mCurrentState);
        mCurrentState = newState;
    }

    public void activate() {
        setState(TriggerState.ACTIVE);
    }

    public void deactivate() {
        setState(TriggerState.INACTIVE);
    }

    private void handleStateChange(TriggerState newState, TriggerState lastState) {
        for (TriggerStateHandler handler : mTriggerStateHandlers) {
            handler.handleStateChange(newState, lastState);
        }
    }
}
