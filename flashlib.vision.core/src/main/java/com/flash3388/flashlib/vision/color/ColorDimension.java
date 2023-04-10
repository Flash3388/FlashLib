package com.flash3388.flashlib.vision.color;


public enum ColorDimension {
    RED(0, 255),
    GREEN(0, 255),
    BLUE(0, 255),
    HUE(0, 180),
    SATURATION(0, 255),
    VALUE(0, 255)
    ;

    private final DimensionRange mValueRange;

    ColorDimension(int min, int max) {
        mValueRange = new DimensionRange(this, min, max);
    }

    public DimensionRange getValueRange() {
        return mValueRange;
    }
}
