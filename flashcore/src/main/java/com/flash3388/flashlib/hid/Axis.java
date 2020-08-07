package com.flash3388.flashlib.hid;

import com.flash3388.flashlib.control.Invertable;

import java.util.function.DoubleSupplier;

public interface Axis extends DoubleSupplier, Invertable {

    @SuppressWarnings("ClassReferencesSubclass")
    default LimitedAxis limit(double valueThreshold, double maxValue) {
        return new LimitedAxis(this, valueThreshold, maxValue);
    }

    Button asButton(double threshold);
}
