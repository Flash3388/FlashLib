package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.time.SystemNanoClock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TestSequentialActionGroup extends SequentialActionGroup {

    private final Consumer<Object> mOutputConsumer;

    public TestSequentialActionGroup(TestActionParams params) {
        super(params.getScheduler(), new SystemNanoClock(), new ArrayList<>(), new ArrayDeque<>());
        mOutputConsumer = params.getOutputConsumer();
    }

    protected final void output(Object object) {
        mOutputConsumer.accept(object);
    }
}
