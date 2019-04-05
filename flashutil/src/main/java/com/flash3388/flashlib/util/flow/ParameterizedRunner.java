package com.flash3388.flashlib.util.flow;

public interface ParameterizedRunner<T> {

    boolean isRunning();

    void start(T param);
    void stop();
}
