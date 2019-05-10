package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Action;

import java.util.function.BooleanSupplier;

public class GenericaActionBuilder {

    private Runnable mOnInitialize;
    private Runnable mOnExecute;
    private BooleanSupplier mIsFinished;
    private Runnable mOnEnd;
    private Runnable mOnInterrupted;

    private boolean mRunOnEndWhenInterrupted;

    public GenericaActionBuilder() {
        mRunOnEndWhenInterrupted = false;
    }

    public GenericaActionBuilder onInitialize(Runnable runnable) {
        mOnInitialize = runnable;
        return this;
    }

    public GenericaActionBuilder onExecute(Runnable runnable) {
        mOnExecute = runnable;
        return this;
    }

    public GenericaActionBuilder isFinished(BooleanSupplier isFinished) {
        mIsFinished = isFinished;
        return this;
    }

    public GenericaActionBuilder onEnd(Runnable runnable) {
        mOnEnd = runnable;
        return this;
    }

    public GenericaActionBuilder onInterrupted(Runnable runnable) {
        mOnInterrupted = runnable;
        return this;
    }

    public GenericaActionBuilder runOnEndWhenInterrupted() {
        mRunOnEndWhenInterrupted = true;
        return this;
    }

    public Action build() {
        return new GenericAction(mOnInitialize,
                mOnExecute,
                mIsFinished,
                mOnEnd,
                mRunOnEndWhenInterrupted ? mOnEnd : mOnInterrupted);
    }
}
