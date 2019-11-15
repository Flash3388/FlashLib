package com.flash3388.flashlib.robot.scheduling.actions;

import java.util.function.BooleanSupplier;

public class GenericActionBuilder extends ActionBuilder<GenericActionBuilder> {

    private Runnable mOnInitialize;
    private Runnable mOnExecute;
    private BooleanSupplier mIsFinished;
    private Runnable mOnEnd;
    private Runnable mOnInterrupted;

    private boolean mRunOnEndWhenInterrupted;

    public GenericActionBuilder() {
        mRunOnEndWhenInterrupted = false;
    }

    public GenericActionBuilder onInitialize(Runnable runnable) {
        mOnInitialize = runnable;
        return this;
    }

    public GenericActionBuilder onExecute(Runnable runnable) {
        mOnExecute = runnable;
        return this;
    }

    public GenericActionBuilder isFinished(BooleanSupplier isFinished) {
        mIsFinished = isFinished;
        return this;
    }

    public GenericActionBuilder onEnd(Runnable runnable) {
        mOnEnd = runnable;
        return this;
    }

    public GenericActionBuilder onInterrupted(Runnable runnable) {
        mOnInterrupted = runnable;
        return this;
    }

    public GenericActionBuilder runOnEndWhenInterrupted() {
        mRunOnEndWhenInterrupted = true;
        return this;
    }

    @Override
    public Action build() {
        Action action = new GenericAction(mOnInitialize,
                mOnExecute,
                mIsFinished,
                mOnEnd,
                mRunOnEndWhenInterrupted ? mOnEnd : mOnInterrupted);
        action.setTimeout(mTimeout);
        action.requires(mRequirements);

        return action;
    }

    @Override
    protected GenericActionBuilder thisInstance() {
        return this;
    }
}
