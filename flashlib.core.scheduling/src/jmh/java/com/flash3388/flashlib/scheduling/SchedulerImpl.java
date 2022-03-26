package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.impl.NewSynchronousScheduler;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.logging.Logging;

import java.util.function.Supplier;

public enum SchedulerImpl {
    SINGLE_THREAD(()->new SynchronousScheduler(new SystemNanoClock())),
    NEW_SINGLE_THREAD(()->new NewSynchronousScheduler(new SystemNanoClock(), Logging.stub()))
    ;

    private final Supplier<Scheduler> mGenerator;

    SchedulerImpl(Supplier<Scheduler> generator) {
        mGenerator = generator;
    }

    public Scheduler create() {
        return mGenerator.get();
    }
}
