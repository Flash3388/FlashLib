package com.flash3388.flashlib.vision.processing.color;

import java.util.function.IntSupplier;

public class ColorRange {

    private final IntSupplier mMin;
    private final IntSupplier mMax;

    public ColorRange(IntSupplier min, IntSupplier max) {
        mMin = min;
        mMax = max;
    }

    public int getMin() {
        return mMin.getAsInt();
    }

    public int getMax() {
        return mMax.getAsInt();
    }
}
