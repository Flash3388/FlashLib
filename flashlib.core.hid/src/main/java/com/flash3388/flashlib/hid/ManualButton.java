package com.flash3388.flashlib.hid;

import com.beans.BooleanProperty;
import com.flash3388.flashlib.hid.generic.GenericButtonBase;
import com.flash3388.flashlib.scheduling.GlobalScheduler;
import com.flash3388.flashlib.scheduling.Scheduler;

/**
 * A {@link Button} implementation which is modified directly from user code and has to be
 * manually updated using {@link #setAsBoolean(boolean)}, hence: <em>manual</em>.
 *
 * @since FlashLib 3.0.0
 */
public class ManualButton extends GenericButtonBase implements Button, BooleanProperty {

    private boolean mValue;
    private boolean mIsInverted;

    public ManualButton(Scheduler scheduler) {
        super(scheduler);
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
}
