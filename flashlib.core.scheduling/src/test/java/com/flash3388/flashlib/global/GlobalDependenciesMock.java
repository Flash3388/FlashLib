package com.flash3388.flashlib.global;

import com.flash3388.flashlib.scheduling.Scheduler;

import static org.mockito.Mockito.mock;

public class GlobalDependenciesMock {

    private GlobalDependenciesMock() {
    }

    public static void mockDependencies() {
        GlobalDependencies.setSchedulerInstance(mock(Scheduler.class));
    }
}
