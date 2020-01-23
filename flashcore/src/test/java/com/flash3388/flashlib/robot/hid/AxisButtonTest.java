package com.flash3388.flashlib.robot.hid;


import com.flash3388.flashlib.time.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AxisButtonTest {

    @Test
    public void isDown_valueAboveThreshold_returnsTrue() throws Exception {
        final double THRESHOLD = 0.5;
        final double AXIS_VALUE = 0.8;

        Clock clock = mock(Clock.class);
        Axis axis = mock(Axis.class);
        when(axis.getAsDouble()).thenReturn(AXIS_VALUE);

        AxisButton button = new AxisButton(clock, axis, THRESHOLD);
        assertTrue(button.isDown());
    }

    @Test
    public void isDown_valueBellowThreshold_returnsFalse() throws Exception {
        final double THRESHOLD = 0.5;
        final double AXIS_VALUE = 0.2;

        Clock clock = mock(Clock.class);
        Axis axis = mock(Axis.class);
        when(axis.getAsDouble()).thenReturn(AXIS_VALUE);

        AxisButton button = new AxisButton(clock, axis, THRESHOLD);
        assertFalse(button.isDown());
    }
}