package com.flash3388.flashlib.robot.hid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AxisTest {

    private static final double MARGIN = 0.0001;

    @ParameterizedTest(name = "setThresholdValue({0}) is invalid")
    @ValueSource(doubles = {-0.1, -1.1, 1.1, 2.0})
    public void setValueThreshold_invalidThresholdValue_throwsIllegalArgumentException(double thresholdValue) throws Exception {
        final int CHANNEL = 1;
        final int AXIS = 1;

        assertThrows(IllegalArgumentException.class, ()->{
            LimitedAxis axis = new LimitedAxis(new HidAxis(mock(HidInterface.class), CHANNEL, AXIS));
            axis.setValueThreshold(thresholdValue);
        });
    }

    @ParameterizedTest(name = "axis value {0} -> limit {1} | expected value {2}")
    @MethodSource("axisWithThresholdValues")
    public void get_valueWithThreshold_returnsExpectedValue(double axisValueFromInterface, double thresholdValue, double expectedAxisValue) throws Exception {
        final int CHANNEL = 0;
        final int AXIS = 0;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, axisValueFromInterface);

        LimitedAxis axis = new LimitedAxis(new HidAxis(hidInterface, CHANNEL, AXIS));
        axis.setValueThreshold(thresholdValue);

        double value = axis.get();
        assertEquals(expectedAxisValue, value, MARGIN);
    }

    @Test
    public void get_inverted_returnsValueWithOppositeSide() throws Exception {
        final int CHANNEL = 1;
        final int AXIS = 1;
        final double VALUE = -0.1;

        HidInterface hidInterface = mockInterfaceWithAxisValue(CHANNEL, AXIS, VALUE);

        Axis axis = new HidAxis(hidInterface, CHANNEL, AXIS);
        axis.setInverted(true);

        double value = axis.get();
        assertEquals(-VALUE, value, MARGIN);
    }

    private static HidInterface mockInterfaceWithAxisValue(int channel, int axis, double value) {
        HidInterface hidInterface = mock(HidInterface.class);
        when(hidInterface.getHidAxis(channel, axis)).thenReturn(value);

        return hidInterface;
    }

    private static Stream<Arguments> axisWithThresholdValues() {
        return Stream.of(
                Arguments.of(-0.1, 0.5, 0.0),
                Arguments.of(0.1, 0.5, 0.0),
                Arguments.of(-0.1, 0.05, -0.1),
                Arguments.of(0.1, 0.05, 0.1),
                Arguments.of(-0.1, 0.1, -0.1),
                Arguments.of(0.1, 0.1, 0.1));
    }
}