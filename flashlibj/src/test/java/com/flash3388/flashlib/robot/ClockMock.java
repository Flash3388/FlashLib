package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClockMock {

    public static Clock mockInvalidTimeClock() {
        return mockClockWithTime(Time.INVALID);
    }

    public static Clock mockClockWithTime(Time time) {
        Clock clock = mock(Clock.class);
        when(clock.currentTime()).thenReturn(time);

        return clock;
    }
}
