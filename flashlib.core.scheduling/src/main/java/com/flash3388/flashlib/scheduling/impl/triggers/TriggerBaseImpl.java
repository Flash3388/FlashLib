package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.CancelOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.RunOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.StartOnState;
import com.flash3388.flashlib.scheduling.impl.triggers.handlers.ToggleOnState;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TriggerBaseImpl implements Trigger {

    private final Collection<TriggerStateListener> mTriggerStateListeners;
    private TriggerState mCurrentState;

    public TriggerBaseImpl(Collection<TriggerStateListener> triggerStateListeners,
                           TriggerState currentState) {
        mTriggerStateListeners = triggerStateListeners;
        mCurrentState = currentState;
    }

    public TriggerBaseImpl(TriggerState currentState) {
        this(new ArrayList<>(), currentState);
    }

    public TriggerBaseImpl() {
        this(TriggerState.INACTIVE);
    }

    @Override
    public void whenActive(Action action) {
        addStateListener(new StartOnState(TriggerState.ACTIVE, action));
    }

    @Override
    public void whenActive(Supplier<Action> supplier) {
        addStateListener(new StartOnState(TriggerState.ACTIVE, supplier));
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
    public void whileActive(Supplier<Action> supplier) {
        addStateListener(new RunOnState(TriggerState.ACTIVE, supplier));
    }

    @Override
    public void whenInactive(Action action) {
        addStateListener(new StartOnState(TriggerState.INACTIVE, action));
    }

    @Override
    public void whenInactive(Supplier<Action> supplier) {
        addStateListener(new StartOnState(TriggerState.INACTIVE, supplier));
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

    @Override
    public void whileInactive(Supplier<Action> supplier) {
        addStateListener(new RunOnState(TriggerState.INACTIVE, supplier));
    }

    void setState(TriggerState newState, TriggerActionController controller) {
        TriggerState lastState = mCurrentState;
        if (lastState != newState) {
            mCurrentState = newState;

            for (TriggerStateListener listener : mTriggerStateListeners) {
                listener.onStateChange(newState, lastState, controller);
            }
        }
    }

    void addStateListener(TriggerStateListener handler) {
        mTriggerStateListeners.add(handler);
    }
}
