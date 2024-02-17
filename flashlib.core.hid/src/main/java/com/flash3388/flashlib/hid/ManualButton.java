package com.flash3388.flashlib.hid;

import com.beans.BooleanProperty;
import com.flash3388.flashlib.scheduling.GlobalScheduler;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

/**
 * A {@link Button} implementation which is modified directly from user code and has to be
 * manually updated using {@link #setAsBoolean(boolean)}, hence: <em>manual</em>.
 *
 * @since FlashLib 3.0.0
 */
public class ManualButton implements Button, BooleanProperty {

    private final Trigger mTrigger;
    private boolean mValue;
    private boolean mIsInverted;

    public ManualButton(Scheduler scheduler) {
        mTrigger = scheduler.newTrigger(this);
        mValue = false;
        mIsInverted = false;
    }

    public ManualButton() {
        this(GlobalScheduler.getScheduler());
    }

    @Override
    public Boolean get() {
        return getAsBoolean();
    }

    @Override
    public boolean getAsBoolean() {
        return mValue ^ mIsInverted;
    }

    @Override
    public void set(Boolean value) {
        setAsBoolean(value != null && value);
    }

    @Override
    public void setAsBoolean(boolean value) {
        value ^= mIsInverted;
        mValue = value;
    }

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
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
