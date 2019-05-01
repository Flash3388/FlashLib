package com.flash3388.flashlib.util.flow;

public interface ThrowingParameterizedRunner<T, E extends Exception> {

    boolean isRunning();

    void start(T param) throws E;
    void stop();
}
