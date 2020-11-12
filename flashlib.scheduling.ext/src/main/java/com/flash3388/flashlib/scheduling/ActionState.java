package com.flash3388.flashlib.scheduling;

public interface ActionState {

    boolean isInitialized();
    boolean isCanceled();

    boolean markStarted();
    boolean markInitialized();
    void markCanceled();
    void markFinished();
}
