package com.flash3388.flashlib.scheduling.triggers;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.RunOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.StartOnState;
import com.flash3388.flashlib.scheduling.triggers.handlers.ToggleOnState;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;

public class SchedulerTrigger implements Trigger, Requirement {

    private final WeakReference<Scheduler> mScheduler;
    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;
    private Action mUpdateAction;

    public SchedulerTrigger(Scheduler scheduler, Collection<TriggerStateListener> triggerStateListeners, TriggerState currentState) {
        mScheduler = new WeakReference<>(scheduler);
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = currentState;
        mUpdateAction = null;
    }

    public SchedulerTrigger(TriggerState currentState) {
        this(GlobalDependencies.getScheduler(), new ArrayList<>(), currentState);
    }

    public SchedulerTrigger() {
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

    public void scheduleAutoUpdate(BooleanSupplier activeCondition) {
        stopScheduling();

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        mUpdateAction = new TriggerActivationAction(scheduler, activeCondition, this);
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
