package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.impl.ActionGroupImpl;
import com.flash3388.flashlib.scheduling.impl.GroupPolicy;
import com.flash3388.flashlib.util.logging.Logging;

import java.util.function.Consumer;

public class TestSequentialActionGroup extends ActionGroupImpl {

    private final Consumer<Object> mOutputConsumer;

    public TestSequentialActionGroup(TestActionParams params) {
        super(params.getScheduler(), Logging.stub(), GroupPolicy.sequential());
        mOutputConsumer = params.getOutputConsumer();
    }

    protected final void output(Object object) {
        mOutputConsumer.accept(object);
    }
}
