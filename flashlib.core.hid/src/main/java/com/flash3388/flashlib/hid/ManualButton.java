package com.flash3388.flashlib.hid;

import com.beans.BooleanProperty;
import com.flash3388.flashlib.scheduling.triggers.SchedulerTrigger;

public class ManualButton extends SchedulerTrigger implements Button, BooleanProperty {

    private boolean mIsInverted;

    public ManualButton() {
        mIsInverted = false;

        schedule(this);
    }

    @Override
    public Boolean get() {
        return getAsBoolean();
    }

    @Override
    public boolean getAsBoolean() {
        return isActive() ^ mIsInverted;
    }

    @Override
    public void set(Boolean value) {
        setAsBoolean(value != null && value);
    }

    @Override
    public void setAsBoolean(boolean value) {
        value ^= mIsInverted;

        if (value == isActive()) {
            return;
        }

        if (value) {
            activate();
        } else {
            deactivate();
        }
    }

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }
}
