package com.flash3388.flashlib.scheduling;

public enum FinishReason {
    FINISHED(false),
    ERRORED(true),
    CANCELED(true),
    TIMEDOUT(true);

    private final boolean mIsInterruptStop;

    FinishReason(boolean isInterruptStop) {
        mIsInterruptStop = isInterruptStop;
    }

    public boolean isInterrupt() {
        return mIsInterruptStop;
    }
}
