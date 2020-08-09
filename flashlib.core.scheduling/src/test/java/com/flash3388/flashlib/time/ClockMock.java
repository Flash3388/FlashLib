package com.flash3388.flashlib.time;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ClockMock {

    private ClockMock() {
    }

    public static Clock mockInvalidTimeClock() {
        return mockClockWithTime(Time.INVALID);
    }

    public static Clock mockClockWithTime(Time time) {
        Clock clock = mock(Clock.class);
        when(clock.currentTime()).thenReturn(time);

        return clock;
    }
}
