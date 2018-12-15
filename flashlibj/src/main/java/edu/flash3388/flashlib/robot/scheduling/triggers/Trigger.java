package edu.flash3388.flashlib.robot.scheduling.triggers;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.triggers.handlers.CancelOnState;
import edu.flash3388.flashlib.robot.scheduling.triggers.handlers.RunOnState;
import edu.flash3388.flashlib.robot.scheduling.triggers.handlers.StartOnState;
import edu.flash3388.flashlib.robot.scheduling.triggers.handlers.ToggleOnState;

import java.util.ArrayList;
import java.util.Collection;

public class Trigger {

    private final Collection<TriggerStateHandler> mTriggerStateHandlers;

    public Trigger() {
        this(new ArrayList<>());
    }

    public Trigger(Collection<TriggerStateHandler> triggerStateHandlers) {
        mTriggerStateHandlers = triggerStateHandlers;
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

    public void activate() {
        handleState(TriggerState.ACTIVE);
    }

    public void deactivate() {
        handleState(TriggerState.INACTIVE);
    }

    private void handleState(TriggerState state) {
        for (TriggerStateHandler handler : mTriggerStateHandlers) {
            handler.handleState(state);
        }
    }
}
