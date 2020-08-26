package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import org.opencv.core.Range;

public class HsvRangeProcessor implements Processor<CvImage, CvImage> {

    private final HsvColorSettings mHsvColorSettings;
    private final CvProcessing mCvProcessing;

    public HsvRangeProcessor(HsvColorSettings hsvColorSettings, CvProcessing cvProcessing) {
        mHsvColorSettings = hsvColorSettings;
        mCvProcessing = cvProcessing;
    }

    @Override
    public CvImage process(CvImage cvImage) {
        Range hue = colorRangeToRange(mHsvColorSettings.getHue());
        Range saturation = colorRangeToRange(mHsvColorSettings.getSaturation());
        Range value = colorRangeToRange(mHsvColorSettings.getValue());

        mCvProcessing.filterMatColors(cvImage.getMat(), cvImage.getMat(), hue, saturation, value);

        return cvImage;
    }

    private Range colorRangeToRange(ColorRange colorRange) {
        return new Range(colorRange.getMin(), colorRange.getMax());
    }
}
