package com.flash3388.flashlib.vision.processing.color;

public enum ColorDimension {
    RED(0, 255),
    GREEN(0, 255),
    BLUE(0, 255),
    HUE(0, 180),
    SATURATION(0, 255),
    VALUE(0, 255)
    ;

    private final ColorRange mValueRange;

    ColorDimension(ColorRange valueRange) {
        mValueRange = valueRange;
    }

    ColorDimension(int min, int max) {
        this(new ColorRange(min, max));
    }

    public ColorRange getValueRange() {
        return mValueRange;
    }
}
