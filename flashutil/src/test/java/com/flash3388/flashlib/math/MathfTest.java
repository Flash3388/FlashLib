package com.flash3388.flashlib.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathfTest {

    private static final double EQUAL_MARGIN = 0.0001;

    @ParameterizedTest(name = "translateAngle({0}) = {1}")
    @MethodSource(value = "translateAngleArguments")
    public void translateAngle_ofParameters_producesExpectedResult(double angleToTranslate, double expectedResult) throws Exception {
        double translated = Mathf.translateAngle(angleToTranslate);
        assertEquals(expectedResult, translated, EQUAL_MARGIN);
    }

    @ParameterizedTest(name = "translateInRange({0}, {1}, {2}) = {3}")
    @MethodSource(value = "translateInRangeArguments")
    public void translateAngle_ofParameters_producesExpectedResult(double valueToTranslate, double rangeToTranslateIn, boolean shouldForcePositive, double expectedResult) throws Exception {
        double translated = Mathf.translateInRange(valueToTranslate, rangeToTranslateIn, shouldForcePositive);
        assertEquals(expectedResult, translated, EQUAL_MARGIN);
    }

    @ParameterizedTest(name = "shortestAngularDistance({0}, {1}) = {2}")
    @MethodSource(value = "angularDistanceArguments")
    public void shortestAngularDistance_ofParameters_producesExpectedResult(double current, double last, double expectedResult) throws Exception {
        double distance = Mathf.shortestAngularDistance(current, last);
        assertEquals(expectedResult, distance, EQUAL_MARGIN);
    }

    private static Stream<Arguments> translateAngleArguments() {
        return Stream.of(
                Arguments.of(0.0, 0.0),
                Arguments.of(30.0, 30.0),
                Arguments.of(180.0, 180.0),
                Arguments.of(280.0, 280.0),
                Arguments.of(360.0, 0.0),
                Arguments.of(-180.0, 180.0),
                Arguments.of(480.0, 120.0),
                Arguments.of(1080.0, 0.0),
                Arguments.of(-480.0, 240.0));
    }

    private static Stream<Arguments> translateInRangeArguments() {
        return Stream.of(
                Arguments.of(0.0, 360.0, true, 0.0),
                Arguments.of(30.0, 360.0, true, 30.0),
                Arguments.of(180.0, 360.0, true, 180.0),
                Arguments.of(280.0, 360.0, true, 280.0),
                Arguments.of(360.0, 360.0, true, 0.0),
                Arguments.of(-180.0, 360.0, true, 180.0),
                Arguments.of(480.0, 360.0, true, 120.0),
                Arguments.of(1080.0, 360.0, true, 0.0),
                Arguments.of(-480.0, 360.0, true, 240.0),
                Arguments.of(-480.0, 360.0, false, -120.0),
                Arguments.of(-100.0, 5.0, true, 0.0));
    }

    private static Stream<Arguments> angularDistanceArguments() {
        return Stream.of(
                Arguments.of(0.0, 0.0, 0.0),
                Arguments.of(180.0, 0.0, 180.0),
                Arguments.of(270.0, 0.0, 90.0),
                Arguments.of(90.0, 360.0, 90.0),
                Arguments.of(90.0, 0.0, 90.0));
    }
}