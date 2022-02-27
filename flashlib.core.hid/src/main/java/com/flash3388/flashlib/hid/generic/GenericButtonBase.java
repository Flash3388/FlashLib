package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

public abstract class GenericButtonBase implements Button, Trigger {

    private final Trigger mTrigger;

    public GenericButtonBase(Scheduler scheduler) {
        mTrigger = scheduler.newTrigger(this);
    }

    public GenericButtonBase() {
        this(GlobalDependencies.getScheduler());
    }

    @Override
    public void whenActive(Action action) {
        mTrigger.whenActive(action);
    }

    @Override
    public void cancelWhenActive(Action action) {
        mTrigger.cancelWhenActive(action);
    }

    @Override
    public void toggleWhenActive(Action action) {
        mTrigger.toggleWhenActive(action);
    }

    @Override
    public void whileActive(Action action) {
        mTrigger.whileActive(action);
    }

    @Override
    public void whenInactive(Action action) {
        mTrigger.whenInactive(action);
    }

    @Override
    public void cancelWhenInactive(Action action) {
        mTrigger.cancelWhenInactive(action);
    }

    @Override
    public void toggleWhenInactive(Action action) {
        mTrigger.toggleWhenInactive(action);
    }

    @Override
    public void whileInactive(Action action) {
        mTrigger.whileInactive(action);
    }
}
