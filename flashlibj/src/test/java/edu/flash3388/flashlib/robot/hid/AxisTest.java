package edu.flash3388.flashlib.robot.hid;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AxisTest {

    private static final double MARGIN = 0.0001;

    @Test
    public void get_valueBellowThreshold_returnsZero() throws Exception {
        final int CHANNEL = 0;
        final int AXIS = 0;
        final double VALUE = 0.1;
        final double THRESHOLD = 0.2;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
        axis.setValueThreshold(THRESHOLD);

        double value = axis.get();
        assertEquals(0.0, value, MARGIN);
    }

    @Test
    public void get_valueNegativeBellowThreshold_returnsZero() throws Exception {
        final int CHANNEL = 0;
        final int AXIS = 0;
        final double VALUE = -0.1;
        final double THRESHOLD = 0.2;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
        axis.setValueThreshold(THRESHOLD);

        double value = axis.get();
        assertEquals(0.0, value, MARGIN);
    }

    @Test
    public void get_valueAboveThreshold_returnsValue() throws Exception {
        final int CHANNEL = 1;
        final int AXIS = 1;
        final double VALUE = 0.1;
        final double THRESHOLD = 0.05;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
        axis.setValueThreshold(THRESHOLD);

        double value = axis.get();
        assertEquals(VALUE, value, MARGIN);
    }

    @Test
    public void get_valueNegativeAboveThreshold_returnsValue() throws Exception {
        final int CHANNEL = 1;
        final int AXIS = 1;
        final double VALUE = -0.1;
        final double THRESHOLD = 0.05;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
        axis.setValueThreshold(THRESHOLD);

        double value = axis.get();
        assertEquals(VALUE, value, MARGIN);
    }

    @Test
    public void get_inverted_returnsValueWithOppositeSide() throws Exception {
        final int CHANNEL = 1;
        final int AXIS = 1;
        final double VALUE = -0.1;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
        axis.setInverted(true);

        double value = axis.get();
        assertEquals(-VALUE, value, MARGIN);
    }

    private HidInterface mockInterfaceWithAxisValue(int channel, int axis, double value) {
        HidInterface hidInterface = mock(HidInterface.class);
        when(hidInterface.getHidAxis(channel, axis)).thenReturn(value);

        return hidInterface;
    }
}