package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public abstract class SequentialAction extends Action {

    private Action mRunNext;

    protected SequentialAction() {
        mRunNext = null;
    }

    protected SequentialAction(Scheduler scheduler, Clock clock) {
        super(scheduler, clock, Time.INVALID);
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
