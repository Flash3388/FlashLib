package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.Trigger;

public abstract class GenericButtonBase implements Button, Trigger {

    private final Trigger mTrigger;

    public GenericButtonBase(Scheduler scheduler) {
        mTrigger = scheduler.newTrigger(this);
    }

    public GenericButtonBase() {
        this(GlobalDependencies.getScheduler());
    }

    @Override
    public void whenActive(ActionInterface action) {
        mTrigger.whenActive(action);
    }

    @Override
    public void cancelWhenActive(ActionInterface action) {
        mTrigger.cancelWhenActive(action);
    }

    @Override
    public void toggleWhenActive(ActionInterface action) {
        mTrigger.toggleWhenActive(action);
    }

    @Override
    public void whileActive(ActionInterface action) {
        mTrigger.whileActive(action);
    }

    @Override
    public void whenInactive(ActionInterface action) {
        mTrigger.whenInactive(action);
    }

    @Override
    public void cancelWhenInactive(ActionInterface action) {
        mTrigger.cancelWhenInactive(action);
    }

    @Override
    public void toggleWhenInactive(ActionInterface action) {
        mTrigger.toggleWhenInactive(action);
    }

    @Override
    public void whileInactive(ActionInterface action) {
        mTrigger.whileInactive(action);
    }
}
