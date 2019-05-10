package com.flash3388.flashlib.robot.scheduling;

public abstract class SequentialAction extends Action {

    private Action mRunNext;

    protected SequentialAction() {
        mRunNext = null;
    }

    protected final void runNext(Action action) {
        mRunNext = action;
    }

    /*package*/ final Action getRunNext() {
        return mRunNext;
    }

    /*package*/ final void resetRunNext() {
        mRunNext = null;
    }
}
