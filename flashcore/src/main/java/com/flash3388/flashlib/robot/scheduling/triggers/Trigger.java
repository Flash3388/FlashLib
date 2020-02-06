package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Requirement;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.RunOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.StartOnState;
import com.flash3388.flashlib.robot.scheduling.triggers.handlers.ToggleOnState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;

public class Trigger implements Requirement {

    private final Scheduler mScheduler;
    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;
    private Action mUpdateAction;

    public Trigger(Scheduler scheduler, Collection<TriggerStateListener> triggerStateListeners, TriggerState currentState) {
        mScheduler = scheduler;
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = currentState;
        mUpdateAction = null;
    }

    public Trigger(TriggerState currentState) {
        this(RunningRobot.getInstance().getScheduler(), new ArrayList<>(), currentState);
    }

    public Trigger() {
        this(TriggerState.INACTIVE);
    }

    public void activate() {
        setState(TriggerState.ACTIVE);
    }

    public void deactivate() {
        setState(TriggerState.INACTIVE);
    }

    public boolean isActive() {
        return mCurrentState == TriggerState.ACTIVE;
    }

    public void schedule(BooleanSupplier activeCondition) {
        stopScheduling();

        mUpdateAction = new TriggerActivationAction(mScheduler, activeCondition, this);
        mUpdateAction.start();
    }

    public void stopScheduling() {
        if (mUpdateAction != null) {
            mUpdateAction.cancel();
            mUpdateAction = null;
        }
    }

    public void addStateListener(TriggerStateListener handler) {
        mTriggerStateListeners.add(handler);
    }

    public void whenActive(Action action) {
        addStateListener(new StartOnState(TriggerState.ACTIVE, action));
    }

    public void cancelWhenActive(Action action) {
        addStateListener(new CancelOnState(TriggerState.ACTIVE, action));
    }

    public void toggleWhenActive(Action action) {
        addStateListener(new ToggleOnState(TriggerState.ACTIVE, action));
    }

    public void whileActive(Action action) {
        addStateListener(new RunOnState(TriggerState.ACTIVE, action));
    }

    public void whenInactive(Action action) {
        addStateListener(new StartOnState(TriggerState.INACTIVE, action));
    }

    public void cancelWhenInactive(Action action) {
        addStateListener(new CancelOnState(TriggerState.INACTIVE, action));
    }

    void setState(TriggerState newState) {
        if (mCurrentState == newState) {
            updateSameState(mCurrentState);
        } else {
            handleStateChange(newState, mCurrentState);
            mCurrentState = newState;
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
