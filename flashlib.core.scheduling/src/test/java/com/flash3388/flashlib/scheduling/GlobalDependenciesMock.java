package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.GlobalScheduler;
import com.flash3388.flashlib.scheduling.Scheduler;

import static org.mockito.Mockito.mock;

public class GlobalDependenciesMock {

    private GlobalDependenciesMock() {
    }

    public static void mockDependencies() {
        GlobalScheduler.setSchedulerInstance(mock(Scheduler.class));
    }
}
