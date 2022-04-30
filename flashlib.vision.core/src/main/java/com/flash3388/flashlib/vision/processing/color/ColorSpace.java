package com.flash3388.flashlib.vision.processing.color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ColorSpace {
    RGB(ColorDimension.RED, ColorDimension.GREEN, ColorDimension.BLUE),
    BGR(ColorDimension.BLUE, ColorDimension.GREEN, ColorDimension.RED),
    HSV(ColorDimension.HUE, ColorDimension.SATURATION, ColorDimension.VALUE)
    ;

    private final List<ColorDimension> mDimensions;

    ColorSpace(List<ColorDimension> dimensions) {
        mDimensions = Collections.unmodifiableList(dimensions);
    }

    ColorSpace(ColorDimension... dimensions) {
        this(Arrays.asList(dimensions));
    }

    public List<ColorDimension> getDimensions() {
        return mDimensions;
    }
}
