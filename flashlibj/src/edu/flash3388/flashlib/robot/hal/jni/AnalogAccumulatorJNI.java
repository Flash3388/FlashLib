package edu.flash3388.flashlib.robot.hal.jni;

public class AnalogAccumulatorJNI {

    public static native int enableAnalogInputAccumulator(int inputPortHandle, boolean enable);

    public static native void setAnalogInputAccumulatorCenter(int inputPortHandle, int center);

    public static native void resetAnalogInputAccumulator(int inputPortHandle);
    public static native long getAnalogInputAccumulatorValue(int inputPortHandle);
    public static native int getAnalogInputAccumulatorCount(int inputPortHandle);
}
