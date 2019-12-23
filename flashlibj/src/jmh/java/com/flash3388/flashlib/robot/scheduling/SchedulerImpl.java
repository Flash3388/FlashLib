package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.time.SystemNanoClock;

import java.util.function.Supplier;

public enum SchedulerImpl {
    SINGLE_THREAD(()->new SingleThreadScheduler(new SystemNanoClock()))
    ;

    private final Supplier<Scheduler> mGenerator;

    SchedulerImpl(Supplier<Scheduler> generator) {
        mGenerator = generator;
    }

    public Scheduler create() {
        return mGenerator.get();
    }
}
