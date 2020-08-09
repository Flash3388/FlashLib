package com.flash3388.flashlib.scheduling;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SchedulerModeMock {
    private SchedulerModeMock() {}

    public static SchedulerMode mockDisabledMode() {
        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(true);
        return mode;
    }

    public static SchedulerMode mockNotDisabledMode() {
        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(false);
        return mode;
    }
}
