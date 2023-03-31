package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.ManualTrigger;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.RunOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.StartOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.ToggleOnState;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

public class TriggerImpl implements ManualTrigger, Requirement {

    private final WeakReference<Scheduler> mScheduler;
    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;

    public TriggerImpl(Scheduler scheduler, Collection<TriggerStateListener> triggerStateListeners,
                       TriggerState currentState) {
        mScheduler = new WeakReference<>(scheduler);
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = currentState;
    }

    public TriggerImpl(TriggerState currentState, Scheduler scheduler) {
        this(scheduler, new ArrayList<>(), currentState);
    }

    public TriggerImpl(Scheduler scheduler) {
        this(TriggerState.INACTIVE, scheduler);
    }

    public TriggerImpl() {
        this(GlobalDependencies.getScheduler());
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
    public void whenActive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new StartOnState(TriggerState.ACTIVE, actualAction));
    }

    @Override
    public void cancelWhenActive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new CancelOnState(TriggerState.ACTIVE, actualAction));
    }

    @Override
    public void toggleWhenActive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new ToggleOnState(TriggerState.ACTIVE, actualAction));
    }

    @Override
    public void whileActive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new RunOnState(TriggerState.ACTIVE, actualAction));
    }

    @Override
    public void whenInactive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new StartOnState(TriggerState.INACTIVE, actualAction));
    }

    @Override
    public void cancelWhenInactive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new CancelOnState(TriggerState.INACTIVE, actualAction));
    }

    @Override
    public void toggleWhenInactive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new ToggleOnState(TriggerState.INACTIVE, actualAction));
    }

    @Override
    public void whileInactive(ActionInterface action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        ConfiguredAction actualAction = scheduler.newAction(action, new ActionConfiguration());
        addStateListener(new RunOnState(TriggerState.INACTIVE, actualAction));
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
