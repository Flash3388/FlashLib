package com.flash3388.flashlib.vision.color;

import java.util.Collections;
import java.util.List;

public class ColorRange {

    private final ColorSpace mColorSpace;
    private final List<DimensionRange> mDimensions;

    public ColorRange(ColorSpace colorSpace, List<DimensionRange> dimensions) {
        mColorSpace = colorSpace;
        mDimensions = Collections.unmodifiableList(dimensions);
    }

    public ColorSpace getColorSpace() {
        return mColorSpace;
    }

    public List<DimensionRange> getDimensions() {
        return mDimensions;
    }

    public int[] getMinAsArray() {
        int[] values = {0, 0, 0, 0};
        for (int i = 0; i < mDimensions.size(); i++) {
            values[i] = mDimensions.get(i).getMin();
        }

        return values;
    }

    public int[] getMaxAsArray() {
        int[] values = {0, 0, 0, 0};
        for (int i = 0; i < mDimensions.size(); i++) {
            values[i] = mDimensions.get(i).getMax();
        }

        return values;
    }
}
