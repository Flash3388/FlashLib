package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Action;

import java.util.function.BooleanSupplier;

public class GenericAction extends Action {

    private final Runnable mOnInitialize;
    private final Runnable mOnExecute;
    private final BooleanSupplier mIsFinished;
    private final Runnable mOnEnd;
    private final Runnable mOnInterrupted;

    public GenericAction(Runnable onInitialize, Runnable onExecute, BooleanSupplier isFinished, Runnable onEnd, Runnable onInterrupted) {
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mIsFinished = isFinished;
        mOnEnd = onEnd;
        mOnInterrupted = onInterrupted;
    }

    @Override
    protected final void initialize() {
        if (mOnInitialize != null) {
            mOnInitialize.run();
        }
    }

    @Override
    protected final void execute() {
        if (mOnExecute != null) {
            mOnExecute.run();
        }
    }

    @Override
    protected final boolean isFinished() {
        return mIsFinished != null && mIsFinished.getAsBoolean();
    }

    @Override
    protected final void end() {
        if (mOnEnd != null) {
            mOnEnd.run();
        }
    }

    @Override
    protected final void interrupted() {
        if (mOnInterrupted != null) {
            mOnInterrupted.run();
        }
    }
}
