package com.flash3388.flashlib.scheduling;

public class SchedulerModeImpl implements SchedulerMode {

    private final boolean mIsDisabled;

    public SchedulerModeImpl(boolean isDisabled) {
        mIsDisabled = isDisabled;
    }

    @Override
    public boolean isDisabled() {
        return mIsDisabled;
    }
}
