package edu.flash3388.flashlib.robot.hid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AxisTest {

    private static final double MARGIN = 0.0001;

    @RunWith(Parameterized.class)
    public static class InvalidThresholdValueTest {

        @Parameterized.Parameter(0)
        public double mThresholdValue;

        @Parameterized.Parameters(name = "invalidThreshold({0})")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {-0.1},
                    {-1.1},
                    {1.1},
                    {2.0}
            });
        }

        @Test(expected = IllegalArgumentException.class)
        public void setValueThreshold_invalidThresholdValue_throwsIllegalArgumentException() throws Exception {
            final int CHANNEL = 1;
            final int AXIS = 1;

            Axis axis = new Axis(mock(HidInterface.class), CHANNEL, AXIS);
            axis.setValueThreshold(mThresholdValue);
        }
    }

    @RunWith(Parameterized.class)
    public static class AxisWithThresholdTest {

        @Parameterized.Parameter(0)
        public double mAxisValueFromInterface;
        @Parameterized.Parameter(1)
        public double mThresholdValue;
        @Parameterized.Parameter(2)
        public double mExpectedAxisValue;

        @Parameterized.Parameters(name = "valueWithThreshold({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {-0.1, 0.5, 0.0},
                    {0.1, 0.5, 0.0},
                    {-0.1, 0.05, -0.1},
                    {0.1, 0.05, 0.1},
                    {-0.1, 0.1, -0.1},
                    {0.1, 0.1, 0.1}
            });
        }

        @Test
        public void get_valueWithThreshold_returnsExcepectedValue() throws Exception {
            final int CHANNEL = 0;
            final int AXIS = 0;

            HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, mAxisValueFromInterface);

            Axis axis = new Axis(hidInterface, CHANNEL, AXIS);
            axis.setValueThreshold(mThresholdValue);

            double value = axis.get();
            assertEquals(mExpectedAxisValue, value, MARGIN);
        }
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

    private static HidInterface mockInterfaceWithAxisValue(int channel, int axis, double value) {
        HidInterface hidInterface = mock(HidInterface.class);
        when(hidInterface.getHidAxis(channel, axis)).thenReturn(value);

        return hidInterface;
    }
}