package com.flash3388.flashlib.scheduling.triggers;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.RunOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.StartOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.ToggleOnState;

import java.util.ArrayList;
import java.util.Collection;

public class TriggerImpl implements ManualTrigger, Requirement {

    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;

    public TriggerImpl(Collection<TriggerStateListener> triggerStateListeners,
                            TriggerState currentState) {
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = currentState;
    }

    public TriggerImpl(TriggerState currentState) {
        this(new ArrayList<>(), currentState);
    }

    public TriggerImpl() {
        this(TriggerState.INACTIVE);
    }

    @Override
    public void activate() {
        setState(TriggerState.ACTIVE);
    }

    @Override
    public void deactivate() {
        setState(TriggerState.INACTIVE);
    }

    @Override
    public boolean isActive() {
        return mCurrentState == TriggerState.ACTIVE;
    }

    @Override
    public void whenActive(Action action) {
        addStateListener(new StartOnState(TriggerState.ACTIVE, action));
    }

    @Override
    public void cancelWhenActive(Action action) {
        addStateListener(new CancelOnState(TriggerState.ACTIVE, action));
    }

    @Override
    public void toggleWhenActive(Action action) {
        addStateListener(new ToggleOnState(TriggerState.ACTIVE, action));
    }

    @Override
    public void whileActive(Action action) {
        addStateListener(new RunOnState(TriggerState.ACTIVE, action));
    }

    @Override
    public void whenInactive(Action action) {
        addStateListener(new StartOnState(TriggerState.INACTIVE, action));
    }

    @Override
    public void cancelWhenInactive(Action action) {
        addStateListener(new CancelOnState(TriggerState.INACTIVE, action));
    }

    @Override
    public void toggleWhenInactive(Action action) {
        addStateListener(new ToggleOnState(TriggerState.INACTIVE, action));
    }

    @Override
    public void whileInactive(Action action) {
        addStateListener(new RunOnState(TriggerState.INACTIVE, action));
    }

    void addStateListener(TriggerStateListener handler) {
        mTriggerStateListeners.add(handler);
    }

    void setState(TriggerState newState) {
        if (mCurrentState != newState) {
            handleStateChange(newState, mCurrentState);
            mCurrentState = newState;
        }
    }

    private void handleStateChange(TriggerState newState, TriggerState lastState) {
        for (TriggerStateListener listener : mTriggerStateListeners) {
            listener.onStateChange(newState, lastState);
        }
    }
}
