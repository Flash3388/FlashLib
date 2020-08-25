package com.flash3388.flashlib.vision.processing.color;

import com.beans.util.function.Suppliers;

import java.util.function.IntSupplier;

public class ColorRange {

    private final IntSupplier mMin;
    private final IntSupplier mMax;

    public ColorRange(IntSupplier min, IntSupplier max) {
        mMin = min;
        mMax = max;
    }

    public ColorRange(int min, int max) {
        this(Suppliers.of(min), Suppliers.of(max));
    }

    public int getMin() {
        return mMin.getAsInt();
    }

    public int getMax() {
        return mMax.getAsInt();
    }
}
