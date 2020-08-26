package com.flash3388.flashlib.scheduling.actions;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GenericAction extends ActionBase {

    public static class Builder {
        private Runnable mOnInitialize;
        private Runnable mOnExecute;
        private BooleanSupplier mIsFinished;
        private Consumer<Boolean> mOnEnd;

        public Builder onInitialize(Runnable runnable) {
            mOnInitialize = runnable;
            return this;
        }

        public Builder onExecute(Runnable runnable) {
            mOnExecute = runnable;
            return this;
        }

        public Builder isFinished(BooleanSupplier supplier) {
            mIsFinished = supplier;
            return this;
        }

        public Builder onEnd(Consumer<Boolean> consumer) {
            mOnEnd = consumer;
            return this;
        }

        public Action build() {
            return new GenericAction(mOnInitialize, mOnExecute, mIsFinished, mOnEnd);
        }
    }

    private final Runnable mOnInitialize;
    private final Runnable mOnExecute;
    private final BooleanSupplier mIsFinished;
    private final Consumer<Boolean> mOnEnd;

    public GenericAction(Runnable onInitialize, Runnable onExecute, BooleanSupplier isFinished, Consumer<Boolean> onEnd) {
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mIsFinished = isFinished;
        mOnEnd = onEnd;
    }

    @Override
    public final void initialize() {
        if (mOnInitialize != null) {
            mOnInitialize.run();
        }
    }

    @Override
    public final void execute() {
        if (mOnExecute != null) {
            mOnExecute.run();
        }
    }

    @Override
    public final boolean isFinished() {
        return mIsFinished != null && mIsFinished.getAsBoolean();
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (mOnEnd != null) {
            mOnEnd.accept(wasInterrupted);
        }
    }
}
