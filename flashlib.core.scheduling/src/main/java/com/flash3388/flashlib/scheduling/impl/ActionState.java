package com.flash3388.flashlib.scheduling.impl;

public interface ActionState {

    boolean isInitialized();
    boolean isRunning();
    boolean isCanceled();

    boolean markStarted();
    boolean markInitialized();
    void markCanceled();
    void markFinished();
}
