package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.impl.SingleThreadedScheduler;
import com.flash3388.flashlib.time.SystemNanoClock;

import java.util.function.Supplier;

public enum SchedulerImpl {
    SINGLE_THREAD(()->new SynchronousScheduler(new SystemNanoClock())),
    NEW_SINGLE_THREAD(()->new SingleThreadedScheduler(new SystemNanoClock()))
    ;

    private final Supplier<Scheduler> mGenerator;

    SchedulerImpl(Supplier<Scheduler> generator) {
        mGenerator = generator;
    }

    public Scheduler create() {
        return mGenerator.get();
    }
}
