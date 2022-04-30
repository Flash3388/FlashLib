package com.flash3388.flashlib.vision.processing.color;

import java.util.Collections;
import java.util.List;

public class ColorConfig {

    private final ColorSpace mSpace;
    private final List<ColorRange> mDimensionFilters;

    public ColorConfig(ColorSpace space, List<ColorRange> dimensionFilters) {
        mSpace = space;
        mDimensionFilters = Collections.unmodifiableList(dimensionFilters);
    }

    public ColorSpace getSpace() {
        return mSpace;
    }

    public List<ColorRange> getDimensionFilters() {
        return mDimensionFilters;
    }
}
