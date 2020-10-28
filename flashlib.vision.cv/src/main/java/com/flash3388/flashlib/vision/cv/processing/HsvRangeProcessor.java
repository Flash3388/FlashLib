package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import org.opencv.core.Mat;
import org.opencv.core.Range;

public class HsvRangeProcessor implements Processor<CvImage, CvImage> {

    private final HsvColorSettings mHsvColorSettings;
    private final CvProcessing mCvProcessing;
    private final boolean mKeepMat;

    private final Mat mThreshold;

    public HsvRangeProcessor(HsvColorSettings hsvColorSettings, CvProcessing cvProcessing, boolean keepMat) {
        mHsvColorSettings = hsvColorSettings;
        mCvProcessing = cvProcessing;
        mKeepMat = keepMat;

        mThreshold = keepMat ? new Mat() : null;
    }

    public HsvRangeProcessor(HsvColorSettings hsvColorSettings, CvProcessing cvProcessing) {
        this(hsvColorSettings, cvProcessing, false);
    }

    @Override
    public CvImage process(CvImage cvImage) {
        Range hue = colorRangeToRange(mHsvColorSettings.getHue());
        Range saturation = colorRangeToRange(mHsvColorSettings.getSaturation());
        Range value = colorRangeToRange(mHsvColorSettings.getValue());

        if (mKeepMat) {
            mCvProcessing.filterMatColors(cvImage.getMat(), mThreshold, hue, saturation, value);
            return new CvImage(mThreshold);
        } else {
            mCvProcessing.filterMatColors(cvImage.getMat(), cvImage.getMat(), hue, saturation, value);
            return cvImage;
        }
    }

    private Range colorRangeToRange(ColorRange colorRange) {
        return new Range(colorRange.getMin(), colorRange.getMax());
    }
}
