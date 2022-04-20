package com.flash3388.flashlib.global;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;

public class GlobalDependenciesMock {

    private GlobalDependenciesMock() {
    }

    public static void mockDependencies() {
        GlobalDependencies.setSchedulerInstance(mock(Scheduler.class));
        GlobalDependencies.setClockInstance(mock(Clock.class));
        GlobalDependencies.setLoggerInstance(mock(Logger.class));
    }

    public static void mockClock(Clock clock) {
        GlobalDependencies.setClockInstance(clock);
    }
}
