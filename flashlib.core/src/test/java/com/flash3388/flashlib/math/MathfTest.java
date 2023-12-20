package com.flash3388.flashlib.math;

import com.flash3388.flashlib.control.Direction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathfTest {

    private static final double EQUAL_MARGIN = 0.0001;

    @ParameterizedTest(name = "translateAngle({0}) = {1}")
    @CsvSource({
            "0.0, 0.0",
            "30.0, 30.0",
            "180.0, 180.0",
            "280.0, 280.0",
            "360.0, 0.0",
            "-180.0, 180.0",
            "480.0, 120.0",
            "1080.0, 0.0",
            "-480.0, 240.0"
    })
    public void translateAngle_ofParameters_producesExpectedResult(double angleToTranslate, double expectedResult) throws Exception {
        double translated = Mathf.translateAngle(angleToTranslate);
        assertEquals(expectedResult, translated, EQUAL_MARGIN);
    }

    @ParameterizedTest(name = "translateInRange({0}, {1}, {2}) = {3}")
    @CsvSource({
            "0.0, 360.0, true, 0.0",
            "30.0, 360.0, true, 30.0",
            "180.0, 360.0, true, 180.0",
            "280.0, 360.0, true, 280.0",
            "360.0, 360.0, true, 0.0",
            "-180.0, 360.0, true, 180.0",
            "480.0, 360.0, true, 120.0",
            "1080.0, 360.0, true, 0.0",
            "-480.0, 360.0, true, 240.0",
            "-480.0, 360.0, false, -120.0",
            "-100.0, 5.0, true, 0.0"
    })
    public void translateAngle_ofParameters_producesExpectedResult(double valueToTranslate, double rangeToTranslateIn, boolean shouldForcePositive, double expectedResult) throws Exception {
        double translated = Mathf.translateInRange(valueToTranslate, rangeToTranslateIn, shouldForcePositive);
        assertEquals(expectedResult, translated, EQUAL_MARGIN);
    }

    @ParameterizedTest(name = "shortestAngularDistanceAbsolute({0}, {1}) = {2}")
    @CsvSource({
            "0.0, 0.0, 0.0",
            "180.0, 0.0, 180.0",
            "270.0, 0.0, 90.0",
            "90.0, 360.0, 90.0",
            "90.0, 0.0, 90.0"
    })
    public void shortestAngularDistanceAbsolute_ofParameters_producesExpectedResult(double from, double to, double expectedResult) throws Exception {
        double distance = Mathf.shortestAngularDistanceAbsolute(from, to);
        assertEquals(expectedResult, distance, EQUAL_MARGIN);
    }

    @ParameterizedTest(name = "motionDirection({0}, {1}) = {2}")
    @CsvSource({
            "180.0, 0.0, 1",
            "270.0, 0.0, 1",
            "90.0, 360.0, -1",
            "90.0, 0.0, -1"
    })
    public void motionDirection_ofParameters_producesExpectedResult(double from, double to, double expectedResult) throws Exception {
        Direction direction = Mathf.shortestAngularDistanceMotionDirection(from, to);
        assertEquals(expectedResult, direction.sign(), EQUAL_MARGIN);
    }
}