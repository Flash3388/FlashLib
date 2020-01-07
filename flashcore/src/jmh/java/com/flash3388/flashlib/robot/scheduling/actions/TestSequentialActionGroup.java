package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.time.SystemNanoClock;

import java.util.function.Consumer;

public class TestSequentialActionGroup extends SequentialActionGroup {

    private final Consumer<Object> mOutputConsumer;

    public TestSequentialActionGroup(TestActionParams params) {
        super(params.getScheduler(), new SystemNanoClock());
        mOutputConsumer = params.getOutputConsumer();
    }

    protected final void output(Object object) {
        mOutputConsumer.accept(object);
    }
}
