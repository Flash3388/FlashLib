package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Scheduler;

import java.util.function.Consumer;

public class TestActionParams {

    private final Scheduler mScheduler;
    private final Consumer<Object> mOutputConsumer;

    public TestActionParams(Scheduler scheduler, Consumer<Object> outputConsumer) {
        mScheduler = scheduler;
        mOutputConsumer = outputConsumer;
    }

    public Scheduler getScheduler() {
        return mScheduler;
    }

    public Consumer<Object> getOutputConsumer() {
        return mOutputConsumer;
    }
}
