package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;

import java.util.function.BooleanSupplier;

public class ConditionalAction extends ActionBase {

    private final Clock mClock;
    private final BooleanSupplier mCondition;
    private Action mTrue;
    private Action mFalse;

    private ActionContext mRunning;

    public ConditionalAction(Clock clock, BooleanSupplier condition, Action aTrue, Action aFalse) {
        mClock = clock;
        mCondition = condition;
        mTrue = aTrue;
        mFalse = aFalse;

        mRunning = null;
    }

    public ConditionalAction(BooleanSupplier condition, Action aTrue, Action aFalse) {
        this(GlobalDependencies.getClock(), condition, aTrue, aFalse);
    }

    @Override
    public void initialize() {
        Action toRun = mCondition.getAsBoolean() ? mTrue : mFalse;
        if (toRun != null) {
            mRunning = new ActionContext(toRun, mClock);
            mRunning.prepareForRun();
        } else {
            mRunning = null;
        }
    }

    @Override
    public void execute() {
        if (mRunning != null && !mRunning.run()) {
            mRunning.runFinished();
            mRunning = null;
        }
    }

    @Override
    public boolean isFinished() {
        return mRunning == null;
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted && mRunning != null) {
            mRunning.runCanceled();
        }
    }

    public ConditionalAction then(Action action) {
        mTrue = action;
        return this;
    }

    public ConditionalAction or(Action action) {
        mFalse = action;
        return this;
    }
}
