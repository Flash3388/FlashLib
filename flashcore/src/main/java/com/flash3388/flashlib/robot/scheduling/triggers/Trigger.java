package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Requirement;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.RunOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.StartOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.ToggleOnState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;

public class Trigger implements Requirement {

    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;
    private Action mUpdateAction;

    public Trigger(Collection<TriggerStateListener> triggerStateListeners, TriggerState initialState) {
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = initialState;
    }

    public Trigger(TriggerState initialState) {
        this(new ArrayList<>(), initialState);
    }

    public Trigger() {
        this(TriggerState.INACTIVE);
    }

    public Trigger addStateListener(TriggerStateListener handler) {
        mTriggerStateListeners.add(handler);

        return this;
    }

    public Trigger whenActive(Action action) {
        return addStateListener(new StartOnState(TriggerState.ACTIVE, action));
    }

    public Trigger cancelWhenActive(Action action) {
        return addStateListener(new CancelOnState(TriggerState.ACTIVE, action));
    }

    public Trigger toggleWhenActive(Action action) {
        return addStateListener(new ToggleOnState(TriggerState.ACTIVE, action));
    }

    public Trigger whileActive(Action action) {
        return addStateListener(new RunOnState(TriggerState.ACTIVE, action));
    }

    public Trigger whenInactive(Action action) {
        return addStateListener(new StartOnState(TriggerState.INACTIVE, action));
    }

    public Trigger cancelWhenInactive(Action action) {
        return addStateListener(new CancelOnState(TriggerState.INACTIVE, action));
    }

    public void setState(TriggerState newState) {
        if (mCurrentState == newState) {
            updateSameState(mCurrentState);
        } else {
            handleStateChange(newState, mCurrentState);
            mCurrentState = newState;
        }
    }

    public void activate() {
        setState(TriggerState.ACTIVE);
    }

    public void deactivate() {
        setState(TriggerState.INACTIVE);
    }

    public void addToScheduler(BooleanSupplier condition) {
        removeFromScheduler();

        mUpdateAction = new TriggerActivationAction(condition, this);
        mUpdateAction.start();
    }

    public void removeFromScheduler() {
        if (mUpdateAction != null) {
            mUpdateAction.cancel();
            mUpdateAction = null;
        }
    }

    private void updateSameState(TriggerState state) {
        for (TriggerStateListener listener : mTriggerStateListeners) {
            listener.updateInState(state);
        }
    }

    private void handleStateChange(TriggerState newState, TriggerState lastState) {
        for (TriggerStateListener listener : mTriggerStateListeners) {
            listener.onStateChange(newState, lastState);
        }
    }
}
