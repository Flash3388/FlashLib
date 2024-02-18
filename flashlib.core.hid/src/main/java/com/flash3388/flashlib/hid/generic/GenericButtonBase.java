package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.scheduling.GlobalScheduler;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

import java.util.function.Supplier;

public abstract class GenericButtonBase implements Button, Trigger {

    private final Trigger mTrigger;

    public GenericButtonBase(Scheduler scheduler) {
        mTrigger = scheduler.newTrigger(this);
    }

    public GenericButtonBase() {
        this(GlobalScheduler.getScheduler());
    }

    @Override
    public void whenActive(Action action) {
        mTrigger.whenActive(action);
    }

    @Override
    public void whenActive(Supplier<Action> supplier) {
        mTrigger.whenActive(supplier);
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
    public void whileActive(Supplier<Action> supplier) {
        mTrigger.whileActive(supplier);
    }

    @Override
    public void whenInactive(Action action) {
        mTrigger.whenInactive(action);
    }

    @Override
    public void whenInactive(Supplier<Action> supplier) {
        mTrigger.whenInactive(supplier);
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

    @Override
    public void whileInactive(Supplier<Action> supplier) {
        mTrigger.whileInactive(supplier);
    }
}
