package com.flash3388.flashlib.vision.cv.processing.color;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvImageProcessor;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import org.opencv.core.Range;

public class HsvRangeProcessor implements CvImageProcessor {

    private final Property<Range> mHueRange;
    private final Property<Range> mSaturationRange;
    private final Property<Range> mValueRange;

    private final CvProcessing mCvProcessing;

    public HsvRangeProcessor(Property<Range> hueRange, Property<Range> saturationRange, Property<Range> valueRange, CvProcessing cvProcessing) {
        mHueRange = hueRange;
        mSaturationRange = saturationRange;
        mValueRange = valueRange;
        mCvProcessing = cvProcessing;
    }

    public HsvRangeProcessor(Property<Range> hueRange, Property<Range> saturationRange, Property<Range> valueRange) {
        this(hueRange, saturationRange, valueRange, new CvProcessing());
    }

    public HsvRangeProcessor(Range hueRange, Range saturationRange, Range valueRange) {
        this(new AtomicProperty<>(hueRange), new AtomicProperty<>(saturationRange), new AtomicProperty<>(valueRange));
    }

    public Property<Range> hueRangeProperty() {
        return mHueRange;
    }

    public Property<Range> saturationRangeProperty() {
        return mSaturationRange;
    }

    public Property<Range> valueRangeProperty() {
        return mValueRange;
    }

    @Override
    public CvImage process(CvImage cvImage) {
        Range hue = mHueRange.get();
        Range saturation = mSaturationRange.get();
        Range value = mValueRange.get();

        mCvProcessing.filterMatColors(cvImage.getMat(), cvImage.getMat(), hue, saturation, value);

        return cvImage;
    }
}
