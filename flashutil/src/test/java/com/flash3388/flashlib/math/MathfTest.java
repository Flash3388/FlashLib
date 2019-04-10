package com.flash3388.flashlib.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class MathfTest {

    @RunWith(Parameterized.class)
    public static class TranslateAngleTest {

        private static final double EQUAL_MARGIN = 0.0001;

        @Parameterized.Parameter(0)
        public double mAngleToTranslate;
        @Parameterized.Parameter(1)
        public double mExpectedResult;

        @Parameterized.Parameters(name = "translateAngle({0}) = {1}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0.0, 0.0},
                    {30.0, 30.0},
                    {180.0, 180.0},
                    {280.0, 280.0},
                    {360.0, 0.0},
                    {-180.0, 180.0},
                    {480.0, 120.0},
                    {1080.0, 0.0},
                    {-480.0, 240.0}
            });
        }

        @Test
        public void translateAngle_ofParameters_producesExpectedResult() throws Exception {
            double translated = Mathf.translateAngle(mAngleToTranslate);
            assertEquals(mExpectedResult, translated, EQUAL_MARGIN);
        }
    }

    @RunWith(Parameterized.class)
    public static class TranslateInRangeTest {

        private static final double EQUAL_MARGIN = 0.0001;

        @Parameterized.Parameter(0)
        public double mValueToTranslate;
        @Parameterized.Parameter(1)
        public double mRangeToTranslateIn;
        @Parameterized.Parameter(2)
        public boolean mShouldForcePositive;
        @Parameterized.Parameter(3)
        public double mExpectedResult;

        @Parameterized.Parameters(name = "translateInRange({0}, {1}, {2}) = {3}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0.0, 360.0, true, 0.0},
                    {30.0, 360.0, true, 30.0},
                    {180.0, 360.0, true, 180.0},
                    {280.0, 360.0, true, 280.0},
                    {360.0, 360.0, true, 0.0},
                    {-180.0, 360.0, true, 180.0},
                    {480.0, 360.0, true, 120.0},
                    {1080.0, 360.0, true, 0.0},
                    {-480.0, 360.0, true, 240.0},
                    {-480.0, 360.0, false, -120.0},
                    {-100.0, 5.0, true, 0.0},
            });
        }

        @Test
        public void translateAngle_ofParameters_producesExpectedResult() throws Exception {
            double translated = Mathf.translateInRange(mValueToTranslate, mRangeToTranslateIn, mShouldForcePositive);
            assertEquals(mExpectedResult, translated, EQUAL_MARGIN);
        }
    }

    @RunWith(Parameterized.class)
    public static class ShortestAngularDistanceTest {

        private static final double EQUAL_MARGIN = 0.0001;

        @Parameterized.Parameter(0)
        public double mCurrent;
        @Parameterized.Parameter(1)
        public double mLast;
        @Parameterized.Parameter(2)
        public double mExpectedResult;

        @Parameterized.Parameters(name = "shortestAngularDistance({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0.0, 0.0, 0.0},
                    {180.0, 0.0, 180.0},
                    {270.0, 0.0, 90.0},
                    {270.0, 360.0, 90.0},
                    {90.0, 360.0, 90.0},
                    {90.0, 0.0, 90.0},
            });
        }

        @Test
        public void shortestAngularDistance_ofParameters_producesExpectedResult() throws Exception {
            double distance = Mathf.shortestAngularDistance(mCurrent, mLast);
            assertEquals(mExpectedResult, distance, EQUAL_MARGIN);
        }
    }
}