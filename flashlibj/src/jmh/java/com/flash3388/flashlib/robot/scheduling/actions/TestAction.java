package com.flash3388.flashlib.robot.scheduling.actions;

import java.util.function.Consumer;

public abstract class TestAction extends ActionBase {

    private final Consumer<Object> mOutputConsumer;

    public TestAction(TestActionParams params) {
        super(params.getScheduler());
        mOutputConsumer = params.getOutputConsumer();
    }

    protected final void output(Object object) {
        mOutputConsumer.accept(object);
    }
}
